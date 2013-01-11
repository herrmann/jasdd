package jasdd.vtree;

/**
 * Helper base class for internal vtree nodes.
 * 
 * @author Ricardo Herrmann
 */
public abstract class InternalTree<T extends Tree> implements Internal<T> {

	private VTree left;
	private T right;

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
	public String toString() {
		return toStringBuilder().toString();
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		InternalTree<T> other = (InternalTree<T>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

}
