package jasdd.bool;

import jasdd.logic.BooleanOperator;
import jasdd.vtree.InternalVTree;

/**
 * Base class with utility methods for all kinds of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public abstract class AbstractSDD implements SDD {

	public static SDD decomposition(final InternalVTree node, final Element... elements) {
		return new DecompositionSDD(node, elements);
	}

	public boolean isTrivial() {
		return isTautology() || isUnsatisfiable();
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public SDD apply(final SDD sdd, final BooleanOperator op) {
		return new OperatorApplication(this, sdd, op).apply();
	}

	@Override
	public SDD and(final SDD sdd) {
		return apply(sdd, new AndOperator());
	}

	@Override
	public SDD or(final SDD sdd) {
		return apply(sdd, new OrOperator());
	}

}
