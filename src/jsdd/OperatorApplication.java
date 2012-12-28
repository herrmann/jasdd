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

	private SDD originalSdd1, originalSdd2, result;
	private BooleanOperator originalOp;

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
		return new ConstantSDD(op.apply(sdd1.getSign(), sdd2.getSign()));
	}

	private SDD apply(final LiteralSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		if (op.equals(new AndOperator())) {
			if (sdd2.getSign()) {
				return new LiteralSDD(sdd1);
			} else {
				return new ConstantSDD(false);
			}
		}
		if (op.equals(new OrOperator())) {
			if (sdd2.getSign()) {
				return new ConstantSDD(true);
			} else {
				return new LiteralSDD(sdd1);
			}
		}
		return null;
	}

	private SDD apply(final LiteralSDD sdd1, final LiteralSDD sdd2, final BooleanOperator op) {
		final Literal literal = sdd1.getLiteral();
		final Literal otherLiteral = sdd2.getLiteral();
		if (literal.equals(otherLiteral)) {
			return new LiteralSDD(sdd1);
		} else if (literal.equals(otherLiteral.opposite())) {
			if (op.equals(new AndOperator())) {
				return new ConstantSDD(false);
			}
			if (op.equals(new OrOperator())) {
				return new ConstantSDD(true);
			}
		} else {
			// TODO: Respect and match vtrees
			if (op.equals(new AndOperator())) {
				return new DecompositionSDD(sdd1.getVTree(), new Element(literal, otherLiteral), new Element(literal.opposite(), false));
			}
			if (op.equals(new OrOperator())) {
				return new DecompositionSDD(sdd1.getVTree(), new Element(literal, true), new Element(literal.opposite(), otherLiteral));
			}
		}
		return null;
	}

	private SDD apply(final DecompositionSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		if (op.equals(new AndOperator())) {
			if (sdd2.getSign()) {
				return new DecompositionSDD(sdd1);
			} else {
				return new ConstantSDD(false);
			}
		}
		if (op.equals(new OrOperator())) {
			if (sdd2.getSign()) {
				return new ConstantSDD(true);
			} else {
				return new DecompositionSDD(sdd1);
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
					new Element(variable, literal.getSign()),
					new Element(variable, false, literal.opposite().getSign()));
		} else {
			decomp = new DecompositionSDD(sdd1.getVTree(), new Element(true, sdd2.getLiteral()));
		}
		return apply(sdd1, decomp, op);
	}

	private SDD apply(final DecompositionSDD sdd1, final DecompositionSDD sdd2, final BooleanOperator op) {
		if (false) {
			// TODO: check if it is in cache
			return null;
		} else {
			final List<Element> elements = new ArrayList<Element>();
			final Map<SDD, Element> subs = new HashMap<SDD, Element>();
			for (final Element e1 : sdd1.expansion()) {
				for (final Element e2 : sdd2.expansion()) {
					SDD prime = e1.getPrime().and(e2.getPrime());
					if (prime instanceof DecompositionSDD) {
						((DecompositionSDD) prime).setVTree((InternalVTree) sdd1.getVTree().getLeft());
					}
					if (prime.isConsistent()) {
						final SDD sub = e1.getSub().apply(e2.getSub(), op);
						if (sub instanceof DecompositionSDD) {
							((DecompositionSDD) sub).setVTree((InternalVTree) sdd1.getVTree().getRight());
						}
						// Apply compression
						if (subs.containsKey(sub)) {
							final Element elem = subs.get(sub);
							elements.remove(elem);
							prime = elem.getPrime().or(prime);
						}
						final Element element = new Element(prime, sub);
						elements.add(element);
						subs.put(sub, element);
					}
				}
			}
			final Element[] elems = new Element[elements.size()];
			elements.toArray(elems);
			return new DecompositionSDD(sdd1.getVTree(), elems);
		}
	}

}
