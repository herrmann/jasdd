package jsdd.stat;

import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;

/**
 * Empty implementation of ASDDVisitor meant to be used as base class for
 * simplifying concrete implementations.
 * 
 * @author Ricardo Herrmann
 */
public abstract class AbstractASDDVisitor<T> extends AbstractSDDVisitor implements ASDDVisitor<T> {

	@Override
	public boolean visit(final DecompositionASDD<T> asdd) {
		return true;
	}

	@Override
	public void postVisit(final DecompositionASDD<T> asdd) {
	}

	@Override
	public boolean visit(final AlgebraicElement<T> elem) {
		return true;
	}

	@Override
	public void postVisit(final AlgebraicElement<T> elem) {
	}

	@Override
	public void visit(final AlgebraicTerminal<T> asdd) {
	}

}
