package jsdd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsdd.vtree.InternalVTree;

/**
 * Boolean operator application with node sharing and cached computation.
 * 
 * @author Ricardo Herrmann
 */
public class OperatorApplication {

	private static final BooleanOperator AND = new AndOperator();
	private static final BooleanOperator OR = new OrOperator();

	private SDD originalSdd1, originalSdd2, result;
	private BooleanOperator originalOp;

	// The components of the new SDD must be new, and the same applies to the
	// caches. No components from the input SDDs must be reused. Global cache
	// comes later when immutability is guaranteed.
	private final Map<SDD, SDD> sddCache = new HashMap<SDD, SDD>();
	private final Map<Element, Element> elementCache = new HashMap<Element, Element>();

	public OperatorApplication(final SDD sdd1, final SDD sdd2, final BooleanOperator op) {
		originalSdd1 = sdd1;
		originalSdd2 = sdd2;
		originalOp = op;
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

	private SDD apply(final LiteralSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		if (op.equals(AND)) {
			if (sdd2.getSign()) {
				return cachedSdd(new LiteralSDD(sdd1));
			} else {
				return cachedSdd(new ConstantSDD(false));
			}
		}
		if (op.equals(OR)) {
			if (sdd2.getSign()) {
				return cachedSdd(new ConstantSDD(true));
			} else {
				return cachedSdd(new LiteralSDD(sdd1));
			}
		}
		return null;
	}

	private SDD apply(final LiteralSDD sdd1, final LiteralSDD sdd2, final BooleanOperator op) {
		final Literal literal = sdd1.getLiteral();
		final Literal otherLiteral = sdd2.getLiteral();
		if (literal.equals(otherLiteral)) {
			return cachedSdd(new LiteralSDD(sdd1));
		} else if (literal.equals(otherLiteral.opposite())) {
			if (op.equals(AND)) {
				return cachedSdd(new ConstantSDD(false));
			}
			if (op.equals(OR)) {
				return cachedSdd(new ConstantSDD(true));
			}
		} else {
			// TODO: Respect and match vtrees
			if (op.equals(AND)) {
				final Element elem1 = cachedElement(new Element(literal, otherLiteral));
				final Element elem2 = cachedElement(new Element(literal.opposite(), false));
				return cachedSdd(new DecompositionSDD(null, elem1, elem2));
			}
			if (op.equals(OR)) {
				final Element elem1 = cachedElement(new Element(literal, true));
				final Element elem2 = cachedElement(new Element(literal.opposite(), otherLiteral));
				return cachedSdd(new DecompositionSDD(null, elem1, elem2));
			}
		}
		return null;
	}

	private SDD apply(final DecompositionSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		if (op.equals(AND)) {
			if (sdd2.getSign()) {
				return cachedSdd(new DecompositionSDD(sdd1));
			} else {
				return cachedSdd(new ConstantSDD(false));
			}
		}
		if (op.equals(OR)) {
			if (sdd2.getSign()) {
				return cachedSdd(new ConstantSDD(true));
			} else {
				return cachedSdd(new DecompositionSDD(sdd1));
			}
		}
		return null;
	}

	private SDD apply(final DecompositionSDD sdd1, final LiteralSDD sdd2, final BooleanOperator op) {
		final Variable variable = sdd2.getLiteral().getVariable();
		DecompositionSDD decomp;
		if (sdd1.getVTree().getLeft().variables().contains(variable)) {
			final Literal literal = sdd2.getLiteral();
			decomp = new DecompositionSDD(sdd1.getVTree(),
					cachedElement(new Element(variable, literal.getSign())),
					cachedElement(new Element(variable, false, literal.opposite().getSign())));
		} else {
			decomp = new DecompositionSDD(sdd1.getVTree(), cachedElement(new Element(true, sdd2.getLiteral())));
		}
		return apply(sdd1, cachedSdd(decomp), op);
	}

	private SDD apply(final DecompositionSDD sdd1, final DecompositionSDD sdd2, final BooleanOperator op) {
		final List<Element> elements = new ArrayList<Element>();
		final Map<SDD, Element> subs = new HashMap<SDD, Element>();
		for (final Element e1 : sdd1.expansion()) {
			for (final Element e2 : sdd2.expansion()) {
				SDD prime = and(e1.getPrime(), e2.getPrime());
				if (prime instanceof DecompositionSDD) {
					((DecompositionSDD) prime).setVTree((InternalVTree) sdd1.getVTree().getLeft());
				}
				if (prime.isConsistent()) {
					final SDD sub = apply(e1.getSub(), e2.getSub(), op);
					if (sub instanceof DecompositionSDD) {
						((DecompositionSDD) sub).setVTree((InternalVTree) sdd1.getVTree().getRight());
					}
					// Apply compression
					if (subs.containsKey(sub)) {
						final Element elem = subs.get(sub);
						elements.remove(elem);
						prime = or(elem.getPrime(), prime);
					}
					final Element element = cachedElement(new Element(prime, sub));
					elements.add(element);
					subs.put(sub, element);
				}
			}
		}
		final Element[] elems = new Element[elements.size()];
		elements.toArray(elems);
		final DecompositionSDD sdd = new DecompositionSDD(sdd1.getVTree(), elems);
		return cachedSdd(sdd);
	}

	private SDD and(final SDD sdd1, final SDD sdd2) {
		return apply(sdd1, sdd2, AND);
	}

	private SDD or(final SDD sdd1, final SDD sdd2) {
		return apply(sdd1, sdd2, OR);
	}

	private Element cachedElement(final Element element) {
		final Element cached = elementCache.get(element);
		if (null == cached) {
			elementCache.put(element, element);
			return element;
		} else {
			// System.out.println("Element cache hit: " + element);
			return cached;
		}
	}

	private SDD cachedSdd(final SDD sdd) {
		final SDD cached = sddCache.get(sdd);
		if (null == cached) {
			sddCache.put(sdd, sdd);
			return sdd;
		} else {
			// System.out.println("SDD cache hit: " + sdd);
			return cached;
		}
	}

}
