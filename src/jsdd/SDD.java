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

	SDD apply(SDD sdd, BooleanOperator op);

	SDD and(SDD sdd);

	Collection<PairedBox> expansion();

}
