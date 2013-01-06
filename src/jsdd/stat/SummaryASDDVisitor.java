package jsdd.stat;

import java.util.HashSet;
import java.util.Set;

import jsdd.DecompositionSDD;
import jsdd.Element;
import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;

/**
 * Visits an ASDD and creates a summary of it.
 * 
 * @author Ricardo Herrmann
 */
public class SummaryASDDVisitor<T> extends AbstractASDDVisitor<T> {

	private final Summary summary = new Summary();

	private int depth;

	final Set<ASDD<T>> visitedASDD = new HashSet<ASDD<T>>();

	final Set<AlgebraicElement<T>> visitedAlgebraicElements = new HashSet<AlgebraicElement<T>>();

	final Set<DecompositionSDD> visitedDecompositions = new HashSet<DecompositionSDD>();

	final Set<Element> visitedElements = new HashSet<Element>();

	public Summary getSummary() {
		return summary;
	}

	@Override
	public boolean visit(final DecompositionASDD<T> asdd) {
		if (!visited(asdd)) {
			markVisited(asdd);
			summary.updateDepth(++depth);
			summary.increaseAlgebraicDecompositions();
			return true;
		}
		return false;
	}

	@Override
	public void postVisit(final DecompositionASDD<T> asdd) {
		depth--;
	}

	@Override
	public void visit(final AlgebraicTerminal<T> asdd) {
		if (!visited(asdd)) {
			markVisited(asdd);
			summary.increaseAlgebraicTerminals();
		}
	}

	@Override
	public boolean visit(final AlgebraicElement<T> elem) {
		if (!visited(elem)) {
			markVisited(elem);
			summary.increaseAlgebraicElements();
			return true;
		}
		return false;
	}

	@Override
	public boolean visit(DecompositionSDD sdd) {
		if (!visited(sdd)) {
			markVisited(sdd);
			summary.updateDepth(++depth);
			summary.increaseDecompositions();
			return true;
		}
		return false;
	}

	@Override
	public void postVisit(final DecompositionSDD sdd) {
		depth--;
	}

	@Override
	public boolean visit(final Element elem) {
		if (!visited(elem)) {
			markVisited(elem);
			summary.increaseElements();
			return true;
		}
		return false;
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

	private boolean visited(final DecompositionSDD sdd) {
		return visitedDecompositions.contains(sdd);
	}

	private void markVisited(final DecompositionSDD sdd) {
		visitedDecompositions.add(sdd);
	}

	private boolean visited(final Element elem) {
		return visitedElements.contains(elem);
	}

	private void markVisited(final Element elem) {
		visitedElements.add(elem);
	}

}
