package jasdd.stat;

import jasdd.algebraic.ASDD;
import jasdd.visitor.UniqueASDDVisitor;

/**
 * Summary of attributes of an ASDD structure.
 * 
 * @author Ricardo Herrmann
 */
public class Summary {

	private int algebraicTerminals, elements, algebraicElements, decompositions, algebraicDecompositions, depth;

	/**
	 * Construct the summary from a given ASDD reference.
	 * 
	 * @param asdd the ASDD to summarize
	 * @return the summary of attributes
	 */
	public static <T> Summary from(final ASDD<T> asdd) {
		final SummaryASDDVisitor<T> delegate = new SummaryASDDVisitor<T>();
		final UniqueASDDVisitor<T> visitor = new UniqueASDDVisitor<T>(delegate);
		asdd.accept(visitor);
		return delegate.getSummary();
	}

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

	/* package */ void increaseAlgebraicTerminals() {
		algebraicTerminals++;
	}

	/* package */ void increaseElements() {
		elements++;
	}

	/* package */ void increaseAlgebraicElements() {
		algebraicElements++;
	}

	/* package */ void increaseDecompositions() {
		decompositions++;
	}

	/* package */ void increaseAlgebraicDecompositions() {
		algebraicDecompositions++;
	}

	/* package */ void updateDepth(final int depth) {
		if (depth > this.depth) {
			this.depth = depth;
		}
	}

}
