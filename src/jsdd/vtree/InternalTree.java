package jsdd.vtree;

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

}
