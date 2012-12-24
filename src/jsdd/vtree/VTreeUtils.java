package jsdd.vtree;

import jsdd.Variable;
import jsdd.VariableRegistry;

/**
 * Utility methods for building specific types of vtrees.
 * 
 * @author Ricardo Herrmann
 */
public class VTreeUtils {

	public static VTree buildRightLinear(final VariableRegistry vars, final String... names) {
		if (names.length == 0) {
			throw new IllegalArgumentException("List of variable names cannot be empty");
		}
		return buildRightLinear(vars, 0, names);
	}

	private static VTree buildRightLinear(final VariableRegistry vars, final int current, final String... names) {
		final VariableLeaf leaf = new VariableLeaf(vars.register(names[current]));
		if (names.length - current == 1) {
			return leaf;
		} else {
			return new InternalVTree(leaf, buildRightLinear(vars, current + 1, names));
		}
	}

	public static VTree register(final VariableRegistry vars, final String name) {
		final Variable var = vars.register(name);
		return new VariableLeaf(var);
	}

}
