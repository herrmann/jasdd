package jsdd.vtree;

import java.util.ArrayList;
import java.util.Collection;

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

	public static Collection<VTree> dissections(final Variable... vars) {
		final int length = vars.length;
		if (length == 0) {
			throw new IllegalArgumentException("VTrees must contain at least one variable");
		}
		return dissections(vars, 0, length);
	}

	private static Collection<VTree> dissections(final Variable[] vars, final int begin, final int end) {
		if (end - begin <= 1) {
			final Collection<VTree> container = new ArrayList<VTree>(1);
			final VariableLeaf leaf = new VariableLeaf(vars[begin]);
			container.add(leaf);
			return container;
		} else {
			final Collection<VTree> container = new ArrayList<VTree>();
			for (int sep = begin + 1; sep < end; sep++) {
				for (final VTree left : dissections(vars, begin, sep)) {
					for (final VTree right : dissections(vars, sep, end)) {
						final InternalVTree vtree = new InternalVTree(left, right);
						container.add(vtree);
					}
				}
			}
			return container;
		}
	}

}
