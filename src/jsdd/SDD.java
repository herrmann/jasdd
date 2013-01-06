package jsdd;

import java.util.Collection;

import jsdd.stat.SDDVisitor;

import util.StringBuildable;

/**
 * Methods shared by all kinds of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public interface SDD extends Sentence, StringBuildable {

	boolean isFalse();

	boolean isTerminal();

	SDD apply(SDD sdd, BooleanOperator op);

	SDD and(SDD sdd);
	SDD or(SDD sdd);

	Collection<Element> expansion();
	SDD trimmed();

	int size();

	/**
	 * For debugging purposes only.
	 */
	void dump();

	void accept(SDDVisitor visitor);

}
