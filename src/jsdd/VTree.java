package jsdd;

import java.util.Set;

import util.StringBuildable;

/**
 * A binary tree for partitioning variables.
 *  
 * @author Ricardo Herrmann
 */
public abstract class VTree implements StringBuildable {

	public abstract Set<Variable> variables();

	public abstract boolean isLeaf();

	public abstract boolean isRightLinear();

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	public static VTree buildRightLinear(final VariableRegistry vars, final String... names) {
		if (names.length == 0) {
			throw new IllegalArgumentException("List of variable names cannot be empty");
		}
		return buildRightLinear(vars, 0, names);
	}

	private static VTree buildRightLinear(final VariableRegistry vars, final int current, final String... names) {
		final LeafNode leaf = new LeafNode(vars.register(names[current]));
		if (names.length - current == 1) {
			return leaf;
		} else {
			return new InternalNode(leaf, buildRightLinear(vars, current + 1, names));
		}
	}

	public static VTree register(final VariableRegistry vars, final String name) {
		final Variable var = vars.register(name);
		return new LeafNode(var);
	}

}
