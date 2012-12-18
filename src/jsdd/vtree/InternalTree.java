package jsdd.vtree;

public abstract class InternalTree<T> implements Internal<T> {

	@Override
	public VTree getLeft() {
		return null;
	}

	@Override
	public T getRight() {
		return null;
	}

}
