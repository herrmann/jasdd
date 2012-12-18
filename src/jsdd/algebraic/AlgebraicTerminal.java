package jsdd.algebraic;

import jsdd.vtree.ValueLeaf;

public class AlgebraicTerminal<T> implements ASDD<T> {

	private T value;

	public AlgebraicTerminal(final T value) {
		this.value = value;
	}

	@Override
	public T evaluate() {
		return value;
	}

	@Override
	public ValueLeaf getTree() {
		return null;
	}

}
