package jasdd.bool;

import jasdd.logic.BooleanOperator;
import jasdd.logic.Sentence;
import jasdd.util.StringBuildable;
import jasdd.visitor.SDDVisitor;

import java.util.Collection;



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