package jasdd.vtree;

import jasdd.logic.VariableRegistry;

/**
 * Auxiliary super type for internal vtree (algebraic or not) nodes.
 *
 * @author Ricardo Herrmann
 */
public interface Internal<T> extends Tree {

	T build(VTree left, T right);

	VTree getLeft();

	T getRight();

	@Override
	StringBuilder toStringBuilder(VariableRegistry vars);

}
