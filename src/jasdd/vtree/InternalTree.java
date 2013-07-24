package jasdd.vtree;

import jasdd.logic.VariableRegistry;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Helper base class for internal vtree nodes.
 *
 * @author Ricardo Herrmann
 */
public abstract class InternalTree<T extends Tree> implements Internal<T> {

	private final VTree left;
	private final T right;

	public InternalTree(final VTree left, final T right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public VTree getLeft() {
		return left;
	}

	@Override
	public T getRight() {
		return right;
	}

	@Override
	public StringBuilder toStringBuilder() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getLeft().toStringBuilder());
		sb.append(",");
		sb.append(getRight().toStringBuilder());
		sb.append(")");
		return sb;
	}

	@Override
	public StringBuilder toStringBuilder(final VariableRegistry vars) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getLeft().toStringBuilder(vars));
		sb.append(",");
		sb.append(getRight().toStringBuilder(vars));
		sb.append(")");
		return sb;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	public String toString(final VariableRegistry vars) {
		return toStringBuilder(vars).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final
		InternalTree<T> other = (InternalTree<T>) obj;
		if (left == null) {
			if (other.left != null) {
				return false;
			}
		} else if (!left.equals(other.left)) {
			return false;
		}
		if (right == null) {
			if (other.right != null) {
				return false;
			}
		} else if (!right.equals(other.right)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canRotateLeft(final Iterator<Direction> path) {
		if (path.hasNext()) {
			final Direction direction = path.next();
			switch (direction) {
			case LEFT:
				return getLeft().canRotateLeft(path);
			case RIGHT:
				return getRight().canRotateLeft(path);
			}
		}
		return canRotateLeft();
	}

	@Override
	public boolean canRotateRight(final Iterator<Direction> path) {
		if (path.hasNext()) {
			final Direction direction = path.next();
			switch (direction) {
			case LEFT:
				return getLeft().canRotateRight(path);
			case RIGHT:
				return getRight().canRotateRight(path);
			}
		}
		return canRotateRight();
	}

	@Override
	public Tree rotateLeft(final Iterator<Direction> path) {
		if (path.hasNext()) {
			final Direction direction = path.next();
			switch (direction) {
			case LEFT:
				return build((VTree) getLeft().rotateLeft(path), getRight());
			case RIGHT:
				@SuppressWarnings("unchecked")
				final T sub = (T) getRight().rotateLeft(path);
				return build(getLeft(), sub);
			default:
				throw new IllegalStateException();
			}
		} else {
			return rotateLeft();
		}
	}

	@Override
	public Tree rotateRight(final Iterator<Direction> path) {
		if (path.hasNext()) {
			final Direction direction = path.next();
			switch (direction) {
			case LEFT:
				return build((VTree) getLeft().rotateRight(path), getRight());
			case RIGHT:
				@SuppressWarnings("unchecked")
				final T sub = (T) getRight().rotateRight(path);
				return build(getLeft(), sub);
			default:
				throw new IllegalStateException();
			}
		} else {
			return rotateRight();
		}
	}

	@Override
	public boolean canRotateLeft(final Direction... path) {
		return canRotateLeft(Arrays.asList(path).iterator());
	}

	@Override
	public boolean canRotateRight(final Direction... path) {
		return canRotateRight(Arrays.asList(path).iterator());
	}

	/**
	 * Creates a new internal node corresponding to the structure of the
	 * original (a)vtree after rotating the root node left and sharing sub-(a)vtrees.
	 *
	 * @return an internal node with the (a)vtree rotated left
	 */
	@Override
	public InternalTree<T> rotateLeft() {
		if (!canRotateLeft()) {
			throw new IllegalArgumentException("The given tree cannot be rotated further to the left.");
		} else {
			final InternalTree<T> right = (InternalTree<T>) getRight();
			return (InternalTree<T>) build(new InternalVTree(getLeft(), right.getLeft()), right.getRight());
		}
	}

	/**
	 * Creates a new internal node corresponding to the structure of the
	 * original (a)vtree after rotating the root node right and sharing sub-(a)vtrees.
	 *
	 * @return an internal node with the (a)vtree rotated right
	 */
	@Override
	public InternalTree<T> rotateRight() {
		if (!canRotateRight()) {
			throw new IllegalArgumentException("The given vtree cannot be rotated further to the right.");
		} else {
			final InternalTree<T> left = (InternalTree<T>) getLeft();
			return (InternalTree<T>) build(left.getLeft(), build((VTree) left.getRight(), getRight()));
		}
	}

	@Override
	public boolean canRotateLeft() {
		return !getRight().isLeaf();
	}

	@Override
	public boolean canRotateRight() {
		return !getLeft().isLeaf();
	}

	@Override
	public Tree rotateLeft(final Direction... path) {
		return rotateLeft(Arrays.asList(path).iterator());
	}

	@Override
	public Tree rotateRight(final Direction... path) {
		return rotateRight(Arrays.asList(path).iterator());
	}

}
