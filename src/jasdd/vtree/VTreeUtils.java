package jasdd.vtree;

import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;

import java.util.ArrayList;
import java.util.Collection;


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

	public static Collection<Tree> algebraicDissections(final Variable... vars) {
		final int length = vars.length;
		if (length == 0) {
			throw new IllegalArgumentException("VTrees must contain at least one variable");
		}
		final Tree[] leaves = new Tree[vars.length + 1];
		int i = 0;
		for (final Variable var : vars) {
			leaves[i++] = new VariableLeaf(var);
		}
		leaves[i] = new ValueLeaf();
		return dissections(leaves, 0, length + 1);
	}

	public static Collection<Tree> dissections(final Variable... vars) {
		final int length = vars.length;
		if (length == 0) {
			throw new IllegalArgumentException("VTrees must contain at least one variable");
		}
		final VariableLeaf[] leaves = new VariableLeaf[vars.length];
		int i = 0;
		for (final Variable var : vars) {
			leaves[i++] = new VariableLeaf(var);
		}
		return dissections(leaves, 0, length);
	}

	private static Collection<Tree> dissections(final Tree[] vars) {
		return dissections(vars, 0, vars.length);
	}

	private static Collection<Tree> dissections(final Tree[] vars, final int begin, final int end) {
		if (end - begin <= 1) {
			final Collection<Tree> container = new ArrayList<Tree>(1);
			final Tree leaf = vars[begin];
			container.add(leaf);
			return container;
		} else {
			final Collection<Tree> container = new ArrayList<Tree>();
			for (int sep = begin + 1; sep < end; sep++) {
				for (final Tree left : dissections(vars, begin, sep)) {
					for (final Tree right : dissections(vars, sep, end)) {
						if (vars[end - 1] instanceof ValueLeaf) {
							container.add(new InternalAVTree((VTree) left, (AVTree) right));
						} else {
							container.add(new InternalVTree((VTree) left, (VTree) right));
						}
					}
				}
			}
			return container;
		}
	}

	public static Collection<Tree> dissections(final ArrayList<Integer> order) {
		final Tree[] leaves = new Tree[order.size() + 1];
		int i = 0;
		for (final Integer index : order) {
			leaves[i++] = new VariableLeaf(index);
		}
		leaves[i] = new ValueLeaf();
		return dissections(leaves);
	}

	public static Iterable<Tree> dissect(final ArrayList<Integer> order) {
		final Tree[] leaves = new Tree[order.size() + 1];
		int i = 0;
		for (final Integer index : order) {
			leaves[i++] = new VariableLeaf(index);
		}
		leaves[i] = new ValueLeaf();
		return new DissectionIterator(leaves);
	}

}
