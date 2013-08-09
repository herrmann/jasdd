package jasdd.vtree;

import jasdd.logic.Variable;
import jasdd.util.CloneableArrayIterator;
import jasdd.util.CloneableIterator;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;


/**
 * Internal node of an algebraic vtree.
 *
 * @author Ricardo Herrmann
 */
public class InternalAVTree extends InternalTree<AVTree> implements AVTree, Swappable<InternalAVTree> {

	public InternalAVTree(final VTree left, final AVTree right) {
		super(left, right);
	}

	public InternalAVTree(final VTree left) {
		this(left, new ValueLeaf());
	}

	public InternalAVTree(final Variable var) {
		this(var, new ValueLeaf());
	}

	public InternalAVTree(final Variable var, final AVTree right) {
		this(new VariableLeaf(var), right);
	}

	@Override
	public InternalAVTree build(final VTree left, final AVTree right) {
		return new InternalAVTree(left, right);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Set<Variable> partitionVariables() {
		return getLeft().partitionVariables();
	}

	@Override
	public VariableLeaf leftmostLeaf() {
		return (VariableLeaf) getLeft().leftmostLeaf();
	}

	@Override
	public ValueLeaf rightmostLeaf() {
		return (ValueLeaf) getRight().rightmostLeaf();
	}

	@Override
	public boolean canSwap() {
		return false;
	}

	@Override
	public boolean canSwap(final Direction... path) {
		return canSwap(Arrays.asList(path).iterator());
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
			return canSwap();
		}
	}

	@Override
	public InternalAVTree swap() {
		throw new UnsupportedOperationException("Internal avtree nodes cannot be swapped.");
	}

	@Override
	public InternalAVTree swap(final Direction... path) {
		return swap(CloneableArrayIterator.build(path));
	}

	private static final String INVALID_PATH_FOR_SWAPPING = "Invalid path for swapping";

	@Override
	public InternalAVTree swap(final CloneableIterator<Direction> path) {
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
				final AVTree deep = getRight();
				if (deep instanceof InternalAVTree) {
					return build(getLeft(), ((InternalAVTree) deep).swap(path));
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
