package jasdd.stat;

import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.visitor.AbstractASDDVisitor;

/**
 * Visits an ASDD and creates a summary of it.
 * 
 * @author Ricardo Herrmann
 */
public class SummaryASDDVisitor<T> extends AbstractASDDVisitor<T> {

	private final Summary summary = new Summary();

	private int depth;

	public Summary getSummary() {
		return summary;
	}

	@Override
	public boolean visit(final DecompositionASDD<T> asdd) {
		summary.updateDepth(++depth);
		summary.increaseAlgebraicDecompositions();
		return true;
	}

	@Override
	public void postVisit(final DecompositionASDD<T> asdd) {
		depth--;
	}

	@Override
	public void visit(final AlgebraicTerminal<T> asdd) {
		summary.increaseAlgebraicTerminals();
	}

	@Override
	public boolean visit(final AlgebraicElement<T> elem) {
		summary.increaseAlgebraicElements();
		return true;
	}

	@Override
	public boolean visit(DecompositionSDD sdd) {
		summary.updateDepth(++depth);
		summary.increaseDecompositions();
		return true;
	}

	@Override
	public void postVisit(final DecompositionSDD sdd) {
		depth--;
	}

	@Override
	public boolean visit(final Element elem) {
		summary.increaseElements();
		return true;
	}

}
