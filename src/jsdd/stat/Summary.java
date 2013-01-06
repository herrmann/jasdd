package jsdd.stat;

import jsdd.algebraic.ASDD;

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

	public static <T> Summary from(final ASDD<T> asdd) {
		final SummaryASDDVisitor<T> visitor = new SummaryASDDVisitor<T>();
		asdd.accept(visitor);
		return visitor.getSummary();
	}

}
