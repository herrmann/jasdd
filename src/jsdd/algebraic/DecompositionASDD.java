package jsdd.algebraic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jsdd.vtree.InternalAVTree;

public class DecompositionASDD<T> implements ASDD<T> {

	private InternalAVTree avtree;

	private List<AlgebraicElement<T>> elements;

	public DecompositionASDD(final InternalAVTree avtree, final AlgebraicElement<T>... elements) {
		this.avtree = avtree;
		this.elements = new ArrayList<AlgebraicElement<T>>(elements.length);
		for (final AlgebraicElement<T> element : elements) {
			this.elements.add(element);
		}
	}

	public List<AlgebraicElement<T>> getElements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public T evaluate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InternalAVTree getTree() {
		return avtree;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avtree == null) ? 0 : avtree.hashCode());
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
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
		DecompositionASDD<T> other = (DecompositionASDD<T>) obj;
		if (avtree == null) {
			if (other.avtree != null)
				return false;
		} else if (!avtree.equals(other.avtree))
			return false;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public StringBuilder toStringBuilder() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[").append(avtree.toStringBuilder()).append(", (");
		boolean first = true;
		for (final AlgebraicElement<T> elem : elements) {
			if (!first) {
				sb.append(" \\/ ");
			}
			first = false;
			sb.append(elem.toStringBuilder());
		}
		sb.append(")]");
		return sb;
	}

}
