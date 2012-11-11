package jsdd;

/**
 * Base class with utility methods for all kinds of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public abstract class AbstractSDD implements SDD {

	public static SDD decomposition(final VTree node, final PairedBox... elements) {
		return new DecompositionSDD(node, elements);
	}

	public boolean isTrivial() {
		return isTautology() || isUnsatisfiable();
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

}
