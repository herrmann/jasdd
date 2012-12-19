package jsdd.algebraic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jsdd.vtree.InternalAVTree;

public class DecompositionASDD<T> implements ASDD<T> {

	private List<AlgebraicElement<T>> elements;

	public DecompositionASDD(final InternalAVTree avtree, final AlgebraicElement<T>... elements) {
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
		return null;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

}
