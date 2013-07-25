package jasdd.vtree;

import jasdd.logic.Variable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * An internal node of a variable partitioning tree.
 *
 * @author Ricardo Herrmann
 */
public class InternalVTree extends InternalTree<VTree> implements VTree {

	public InternalVTree(final VTree left, final VTree right) {
		super(left, right);
	}

	public InternalVTree(final Variable left, final Variable right) {
		this(new VariableLeaf(left), new VariableLeaf(right));
	}

	public InternalVTree(final int leftIndex, final int rightIndex) {
		this(new VariableLeaf(leftIndex), new VariableLeaf(rightIndex));
	}

	public InternalVTree(final VTree left, final int rightIndex) {
		this(left, new VariableLeaf(rightIndex));
	}

	public InternalVTree(final int leftIndex, final VTree right) {
		this(new VariableLeaf(leftIndex), right);
	}

	public InternalVTree(final Variable leftVar, final VTree right) {
		this(new VariableLeaf(leftVar.getIndex()), right);
	}

	public InternalVTree(final VTree left, final Variable rightVar) {
		this(left, new VariableLeaf(rightVar.getIndex()));
	}

	@Override
	public InternalVTree build(final VTree left, final VTree right) {
		return new InternalVTree(left, right);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isRightLinear() {
		return getLeft().isLeaf() && getRight().isRightLinear();
	}

	@Override
	public Set<Variable> variables() {
		final Set<Variable> vars = new HashSet<Variable>();
		vars.addAll(getLeft().variables());
		vars.addAll(getRight().variables());
		return vars;
	}

	@Override
	public Set<Variable> partitionVariables() {
		final Set<Variable> vars = getLeft().partitionVariables();
		vars.addAll(getRight().partitionVariables());
		return vars;
	}

	@Override
	public VariableLeaf leftmostLeaf() {
		return (VariableLeaf) getLeft().leftmostLeaf();
	}

	@Override
	public VariableLeaf rightmostLeaf() {
		return (VariableLeaf) getRight().rightmostLeaf();
	}

	/**
	 * Creates a new internal node sharing this node's left and right sub-vtrees
	 * but in swapped order.
	 *
	 * @return an internal node with swapped left and right sub-vtrees
	 */
	public InternalVTree swap() {
		return build(getRight(), getLeft());
	}

	@Override
	public boolean canSwap(final Iterator<Direction> path) {
		if (path.hasNext()) {
			final Direction direction = path.next();
			switch (direction) {
			case LEFT:
				return getLeft().canSwap(path);
			case RIGHT:
				return getRight().canSwap(path);
			default:
				throw new IllegalStateException();
			}
		} else {
			return true;
		}
	}

	private static final String INVALID_PATH_FOR_SWAPPING = "Invalid path for swapping";

	public InternalVTree swap(final Iterator<Direction> path) {
		if (path.hasNext()) {
			final Direction direction = path.next();
			if (Direction.LEFT == direction) {
				final VTree deep = getLeft();
				if (deep instanceof InternalVTree) {
					return build(((InternalVTree) deep).swap(path), getRight());
				} else {
					throw new IllegalArgumentException(INVALID_PATH_FOR_SWAPPING);
				}
			} else if (Direction.RIGHT == direction) {
				final VTree deep = getRight();
				if (deep instanceof InternalVTree) {
					return build(getLeft(), ((InternalVTree) deep).swap(path));
				} else {
					throw new IllegalArgumentException(INVALID_PATH_FOR_SWAPPING);
				}
			} else {
				throw new IllegalStateException();
			}
		} else {
			return swap();
		}
	}

}
