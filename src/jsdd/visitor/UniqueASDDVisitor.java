package jsdd.visitor;

import java.util.HashSet;
import java.util.Set;

import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;

/**
 * Visits each component of an ASDD only once, delegating the visiting logic to
 * the wrapped visitor.
 * 
 * @author Ricardo Herrmann
 */
public class UniqueASDDVisitor<T> extends UniqueSDDVisitor implements ASDDVisitor<T> {

	private final Set<ASDD<T>> visitedASDD = new HashSet<ASDD<T>>();

	private final Set<AlgebraicElement<T>> visitedAlgebraicElements = new HashSet<AlgebraicElement<T>>();

	public UniqueASDDVisitor(final ASDDVisitor<T> delegate) {
		super(delegate);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ASDDVisitor<T> getDelegate() {
		return (ASDDVisitor<T>) super.getDelegate();
	}

	@Override
	public boolean visit(final DecompositionASDD<T> asdd) {
		if (!visited(asdd)) {
			markVisited(asdd);
			getDelegate().visit(asdd);
			return true;
		}
		return false;
	}

	@Override
	public void postVisit(final DecompositionASDD<T> asdd) {
		getDelegate().postVisit(asdd);
	}

	@Override
	public void visit(final AlgebraicTerminal<T> asdd) {
		if (!visited(asdd)) {
			markVisited(asdd);
			getDelegate().visit(asdd);
		}
	}

	@Override
	public boolean visit(final AlgebraicElement<T> elem) {
		if (!visited(elem)) {
			markVisited(elem);
			getDelegate().visit(elem);
			return true;
		}
		return false;
	}

	@Override
	public void postVisit(final AlgebraicElement<T> elem) {
		getDelegate().postVisit(elem);
	}

	private boolean visited(final ASDD<T> asdd) {
		return visitedASDD.contains(asdd);
	}

	private void markVisited(final ASDD<T> asdd) {
		visitedASDD.add(asdd);
	}

	private boolean visited(final AlgebraicElement<T> elem) {
		return visitedAlgebraicElements.contains(elem);
	}

	private void markVisited(final AlgebraicElement<T> elem) {
		visitedAlgebraicElements.add(elem);
	}

}
