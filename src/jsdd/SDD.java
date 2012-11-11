package jsdd;

import util.StringBuildable;

public abstract class SDD implements Sentence, StringBuildable {

	public static SDD decomposition(final VTree node, final PairedBox... elements) {
		return new DecompositionSDD(node, elements);
	}

	public boolean isTrivial() {
		return isTautology() || isUnsatisfiable();
	}

	public abstract boolean isFalse();

	public abstract SDD and(SDD sdd);

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

}
