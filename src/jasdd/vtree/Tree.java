package jasdd.vtree;

import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.util.StringBuildable;

import java.util.Set;

/**
 * Super type for algebraic and regular vtrees.
 *
 * @author Ricardo Herrmann
 */
public interface Tree extends StringBuildable {

	boolean isLeaf();

	Set<Variable> partitionVariables();

	Leaf leftmostLeaf();

	Leaf rightmostLeaf();

	StringBuilder toStringBuilder(VariableRegistry vars);

	boolean canRotateLeft();

	boolean canRotateRight();

}
