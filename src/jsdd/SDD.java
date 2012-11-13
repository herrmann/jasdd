package jsdd;

import java.util.Collection;

import util.StringBuildable;

/**
 * Methods shared by all kinds of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public interface SDD extends Sentence, StringBuildable {

	boolean isFalse();

	PairedBox getParent();
	boolean isPrime();
	boolean isSub();
	boolean isTerminal();

	VTree getVTree();

	SDD apply(SDD sdd, BooleanOperator op);
	SDD apply(ConstantSDD sdd, BooleanOperator op);
	SDD apply(LiteralSDD sdd, BooleanOperator op);
	SDD apply(DecompositionSDD sdd, BooleanOperator op);

	SDD and(SDD sdd);

	Collection<PairedBox> expansion();

	/**
	 * For debugging purposes only.
	 */
	void dump();

}
