package jsdd.algebraic;

import jsdd.stat.ASDDVisitor;
import jsdd.vtree.ValueLeaf;

/**
 * The terminal value of an algebraic SDD.
 * 
 * @author Ricardo Herrmann
 */
public class AlgebraicTerminal<T> implements ASDD<T> {

	private T value;

	public AlgebraicTerminal(final T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public T evaluate() {
		return value;
	}

	@Override
	public ValueLeaf getTree() {
		return null;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(value.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		AlgebraicTerminal<T> other = (AlgebraicTerminal<T>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public void accept(final ASDDVisitor<T> visitor) {
		visitor.visit(this);
	}

}
