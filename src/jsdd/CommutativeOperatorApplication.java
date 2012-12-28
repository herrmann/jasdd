package jsdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsdd.vtree.InternalVTree;
import jsdd.vtree.VTree;

/**
 * Boolean operator application with node sharing and cached computation.
 * 
 * @author Ricardo Herrmann
 */
public class CommutativeOperatorApplication {

	private SDD originalSdd1, originalSdd2, result;
	private BooleanOperator originalOp;
	private InternalVTree rootVTree;

	// The components of the new SDD must be new, and the same applies to the
	// caches. No components from the input SDDs must be reused. Global cache
	// comes later when immutability is guaranteed.
	private final Map<SDD, SDD> sddCache = new HashMap<SDD, SDD>();
	private final Map<Element, Element> elementCache = new HashMap<Element, Element>();
	
	public CommutativeOperatorApplication(final SDD sdd1, final SDD sdd2, final BooleanOperator op) {
		originalSdd1 = sdd1;
		originalSdd2 = sdd2;
		originalOp = op;
	}

	public CommutativeOperatorApplication(final SDD sdd1, final SDD sdd2, final BooleanOperator op, final InternalVTree rootVTree) {
		this(sdd1, sdd2, op);
		this.rootVTree = rootVTree;
	}

	public SDD apply() {
		if (null == result) {
			result = apply(originalSdd1, originalSdd2, originalOp);
		}
		return result;
	}

	private SDD apply(final SDD sdd1, final SDD sdd2, final BooleanOperator op) {
		// TODO: Java unfortunately lacks multimethods / multiple dispatch and a
		// Visitor pattern application is currently not a priority.
		if (sdd1 instanceof DecompositionSDD) {
			if (sdd2 instanceof DecompositionSDD) {
				return apply((DecompositionSDD) sdd1, (DecompositionSDD) sdd2, op);
			} else if (sdd2 instanceof LiteralSDD) {
				return apply((DecompositionSDD) sdd1, (LiteralSDD) sdd2, op);
			} else if (sdd2 instanceof ConstantSDD) {
				return apply((DecompositionSDD) sdd1, (ConstantSDD) sdd2, op);
			}
		} else if (sdd1 instanceof LiteralSDD) {
			if (sdd2 instanceof LiteralSDD) {
				return apply((LiteralSDD) sdd1, (LiteralSDD) sdd2, op);
			} else if (sdd2 instanceof ConstantSDD) {
				return apply((LiteralSDD) sdd1, (ConstantSDD) sdd2, op);
			}
		} else if (sdd1 instanceof ConstantSDD && sdd2 instanceof ConstantSDD) {
			return apply((ConstantSDD) sdd1, (ConstantSDD) sdd2, op);
		}
		return apply(sdd2, sdd1, op);
	}

	private SDD apply(final ConstantSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		return cachedSdd(new ConstantSDD(op.apply(sdd1.getSign(), sdd2.getSign())));
	}

	private SDD apply(final LiteralSDD sdd1, final SDD sdd2, final BooleanOperator op) {
		// This is the only operation that needs an externally provided vtree.
		final DecompositionSDD norm = DecompositionSDD.buildNormalized(rootVTree, sdd1.getLiteral());
		final DecompositionSDD cachedNorm = (DecompositionSDD) cachedSdd(norm);
		return apply(cachedNorm, sdd2, op);
	}

	private SDD apply(final DecompositionSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		final boolean sign = sdd2.getSign();
		if (op.equals(new AndOperator())) {
			if (sign) {
				return new DecompositionSDD(sdd1);
			} else {
				return new ConstantSDD(false);
			}
		}
		if (op.equals(new OrOperator())) {
			if (sign) {
				return new ConstantSDD(true);
			} else {
				return new DecompositionSDD(sdd1);
			}
		}
		return null;
	}

	private SDD apply(final DecompositionSDD sdd1, final LiteralSDD sdd2, final BooleanOperator op) {
		final InternalVTree vtree = (InternalVTree) sdd1.getVTree();
		final Literal lit = sdd2.getLiteral();
		final DecompositionSDD norm = DecompositionSDD.buildNormalized(vtree, lit);
		final DecompositionSDD cachedNorm = (DecompositionSDD) cachedSdd(norm);
		return apply(sdd1, cachedNorm, op);
	}

	private SDD apply(final DecompositionSDD sdd1, final DecompositionSDD sdd2, final BooleanOperator op) {
		final VTree vtree = sdd1.getVTree();
		// TODO: Put this in a better place (apply()), but meanwhile kept here for safety
		if (!vtree.equals(sdd2.getVTree())) {
			throw new IllegalArgumentException("Operator application can only be done on SDDs that respect the same vtree (for now)");
		}
		final List<Element> elements = new ArrayList<Element>();
		final Map<SDD, Element> subs = new HashMap<SDD, Element>();
		for (final Element e1 : sdd1.expansion()) {
			for (final Element e2 : sdd2.expansion()) {
				SDD prime = apply(e1.getPrime(), e2.getPrime(), new AndOperator());
				// TODO: is this really necessary?
				/*
				if (prime instanceof DecompositionSDD) {
					((DecompositionSDD) prime).setVTree((InternalVTree) ((InternalVTree) vtree).getLeft());
				}
				*/
				if (prime.isConsistent()) {
					final SDD sub = apply(e1.getSub(), e2.getSub(), op);
					// TODO: is this really necessary?
					/*
					if (sub instanceof DecompositionSDD) {
						((DecompositionSDD) sub).setVTree((InternalVTree) ((InternalVTree) vtree).getRight());
					}
					*/
					// Apply compression
					if (subs.containsKey(sub)) {
						final Element elem = subs.get(sub);
						elements.remove(elem);
						prime = elem.getPrime().or(prime);
					}
					final Element element = cachedElement(new Element(prime, sub));
					elements.add(element);
					subs.put(sub, element);
				}
			}
		}
		final Element[] elems = new Element[elements.size()];
		elements.toArray(elems);
		final DecompositionSDD sdd = new DecompositionSDD(vtree, elems);
		return cachedSdd(sdd);
	}

	private Element cachedElement(final Element element) {
		final Element cached = elementCache.get(element);
		if (null == cached) {
			elementCache.put(element, element);
			return element;
		} else {
			return cached;
		}
	}

	private SDD cachedSdd(final SDD sdd) {
		final SDD cached = sddCache.get(sdd);
		if (null == cached) {
			sddCache.put(sdd, sdd);
			return sdd;
		} else {
			return cached;
		}
	}

}
