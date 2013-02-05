package jasdd.bool;

import jasdd.logic.BooleanOperator;
import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.vtree.InternalVTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		return SDDFactory.getInstance().createConstant(op.apply(sdd1.getSign(), sdd2.getSign()));
	}

	private SDD apply(final LiteralSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		if (op.equals(AND)) {
			if (sdd2.getSign()) {
				return SDDFactory.getInstance().createLiteral(sdd1);
			} else {
				return SDDFactory.getInstance().createConstant(false);
			}
		}
		if (op.equals(OR)) {
			if (sdd2.getSign()) {
				return SDDFactory.getInstance().createConstant(true);
			} else {
				return SDDFactory.getInstance().createLiteral(sdd1);
			}
		}
		return null;
	}

	private SDD apply(final LiteralSDD sdd1, final LiteralSDD sdd2, final BooleanOperator op) {
		final Literal literal = sdd1.getLiteral();
		final Literal otherLiteral = sdd2.getLiteral();
		if (literal.equals(otherLiteral)) {
			return SDDFactory.getInstance().createLiteral(sdd1);
		} else if (literal.equals(otherLiteral.opposite())) {
			if (op.equals(AND)) {
				return SDDFactory.getInstance().createConstant(false);
			}
			if (op.equals(OR)) {
				return SDDFactory.getInstance().createConstant(true);
			}
		} else {
			// TODO: Respect and match vtrees
			final SDDFactory factory = SDDFactory.getInstance();
			if (op.equals(AND)) {
				// createDecomposition takes care of caching elements
				final Element elem1 = factory.createElement(literal, otherLiteral);
				final Element elem2 = factory.createElement(literal.opposite(), false);
				return SDDFactory.getInstance().createDecomposition(null, elem1, elem2);
			}
			if (op.equals(OR)) {
				// createDecomposition takes care of caching elements
				final Element elem1 = factory.createElement(literal, true);
				final Element elem2 = factory.createElement(literal.opposite(), otherLiteral);
				return SDDFactory.getInstance().createDecomposition(null, elem1, elem2);
			}
		}
		return null;
	}

	private SDD apply(final DecompositionSDD sdd1, final ConstantSDD sdd2, final BooleanOperator op) {
		if (op.equals(AND)) {
			if (sdd2.getSign()) {
				return SDDFactory.getInstance().createDecomposition(sdd1);
			} else {
				return SDDFactory.getInstance().createConstant(false);
			}
		}
		if (op.equals(OR)) {
			if (sdd2.getSign()) {
				return SDDFactory.getInstance().createConstant(true);
			} else {
				return SDDFactory.getInstance().createDecomposition(sdd1);
			}
		}
		return null;
	}

	private SDD apply(final DecompositionSDD sdd1, final LiteralSDD sdd2, final BooleanOperator op) {
		final Variable variable = sdd2.getLiteral().getVariable();
		final SDDFactory factory = SDDFactory.getInstance();
		DecompositionSDD decomp;
		if (sdd1.getVTree().getLeft().variables().contains(variable)) {
			final Literal literal = sdd2.getLiteral();
			// createDecomposition takes care of caching elements
			decomp = factory.createDecomposition(sdd1.getVTree(),
					factory.createElement(variable, literal.getSign()),
					factory.createElement(variable, false, literal.opposite().getSign()));
		} else {
			decomp = factory.createDecomposition(sdd1.getVTree(), factory.createElement(true, sdd2.getLiteral()));
		}
		return apply(sdd1, decomp, op);
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
					final Element element = SDDFactory.getInstance().createElement(prime, sub);
					elements.add(element);
					subs.put(sub, element);
				}
			}
		}
		final int size = elements.size();
		// Apply light trimming if possible
		final SDDFactory factory = SDDFactory.getInstance();
		if (size == 1 && elements.get(0).getPrime().equals(factory.createTrue()) && elements.get(0).getSub() instanceof ConstantSDD) {
			return factory.createConstant(((ConstantSDD) elements.get(0).getSub()).getSign());
		} else {
			final Element[] elems = new Element[size];
			elements.toArray(elems);
			return factory.createDecomposition(sdd1.getVTree(), elems);
		}
	}

	private SDD and(final SDD sdd1, final SDD sdd2) {
		return apply(sdd1, sdd2, AND);
	}

	private SDD or(final SDD sdd1, final SDD sdd2) {
		return apply(sdd1, sdd2, OR);
	}

}
