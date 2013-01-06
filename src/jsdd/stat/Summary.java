package jsdd.stat;

import java.util.HashSet;
import java.util.Set;

import jsdd.DecompositionSDD;
import jsdd.Element;
import jsdd.SDD;
import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;

public class Summary {
	
	private int algebraicTerminals, elements, algebraicElements, decompositions, algebraicDecompositions, depth;

	public int getAlgebraicTerminals() {
		return algebraicTerminals;
	}

	public int getElements() {
		return elements;
	}

	public int getAlgebraicElements() {
		return algebraicElements;
	}

	public int getDecompositions() {
		return decompositions;
	}

	public int getAlgebraicDecompositions() {
		return algebraicDecompositions;
	}

	public int getDepth() {
		return depth;
	}

	public void increaseAlgebraicTerminals() {
		algebraicTerminals++;
	}

	public void increaseElements() {
		elements++;
	}

	public void increaseAlgebraicElements() {
		algebraicElements++;
	}

	public void increaseDecompositions() {
		decompositions++;
	}

	public void increaseAlgebraicDecompositions() {
		algebraicDecompositions++;
	}

	public void updateDepth(final int depth) {
		if (depth > this.depth) {
			this.depth = depth;
		}
	}

	public static Summary from(final ASDD<?> asdd) {
		final Summary summary = new Summary();
		summary.visit(asdd, 0);
		return summary;
	}

	public <T> void visit(final ASDD<T> asdd, final int depth) {
		if (!visited(asdd)) {
			markVisited(asdd);
			if (asdd instanceof DecompositionASDD<?>) {
				final DecompositionASDD<T> decomp = (DecompositionASDD<T>) asdd;
				final int newDepth = depth + 1;
				updateDepth(newDepth);
				increaseAlgebraicDecompositions();
				for (final AlgebraicElement<T> elem : decomp.getElements()) {
					visit(elem, newDepth);
				}
			} else if (asdd instanceof AlgebraicTerminal<?>) {
				increaseAlgebraicTerminals();
			}
		}
	}

	private <T> void visit(final AlgebraicElement<T> elem, final int depth) {
		if (!visited(elem)) {
			markVisited(elem);
			increaseAlgebraicElements();
			visit(elem.getPrime(), depth);
			visit(elem.getSub(), depth);
		}
	}

	private void visit(final SDD sdd, final int depth) {
		if (sdd instanceof DecompositionSDD) {
			final DecompositionSDD decomp = (DecompositionSDD) sdd;
			if (!visited(decomp)) {
				markVisited(decomp);
				final int newDepth = depth + 1;
				updateDepth(newDepth);
				increaseDecompositions();
				for (final Element elem : decomp.getElements()) {
					visit(elem, newDepth);
				}
			}
		}
	}

	private void visit(final Element elem, final int depth) {
		if (!visited(elem)) {
			markVisited(elem);
			increaseElements();
			visit(elem.getPrime(), depth);
			visit(elem.getSub(), depth);
		}
	}

	final Set<ASDD> visitedASDD = new HashSet<ASDD>();

	private <T> boolean visited(final ASDD<T> asdd) {
		return visitedASDD.contains(asdd);
	}

	private <T> void markVisited(final ASDD<T> asdd) {
		visitedASDD.add(asdd);
	}

	final Set<AlgebraicElement> visitedAlgebraicElements = new HashSet<AlgebraicElement>();

	private <T> boolean visited(final AlgebraicElement<T> elem) {
		return visitedAlgebraicElements.contains(elem);
	}

	private <T> void markVisited(final AlgebraicElement<T> elem) {
		visitedAlgebraicElements.add(elem);
	}

	final Set<DecompositionSDD> visitedDecompositions = new HashSet<DecompositionSDD>();

	private boolean visited(final DecompositionSDD sdd) {
		return visitedDecompositions.contains(sdd);
	}

	private void markVisited(final DecompositionSDD sdd) {
		visitedDecompositions.add(sdd);
	}

	final Set<Element> visitedElements = new HashSet<Element>();

	private boolean visited(final Element elem) {
		return visitedElements.contains(elem);
	}

	private void markVisited(final Element elem) {
		visitedElements.add(elem);
	}

}
