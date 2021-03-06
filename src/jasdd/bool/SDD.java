package jasdd.bool;

import jasdd.logic.BooleanOperator;
import jasdd.logic.Formula;
import jasdd.logic.Sentence;
import jasdd.logic.Variable;
import jasdd.util.StringBuildable;
import jasdd.visitor.SDDVisitor;
import jasdd.vtree.InternalVTree;

import java.util.Collection;
import java.util.Set;

/**
 * Methods shared by all kinds of SDDs.
 *
 * @author Ricardo Herrmann
 */
public interface SDD extends Sentence, StringBuildable, ComparableSDD {

	boolean isFalse();

	boolean isTerminal();

	SDD apply(SDD sdd, BooleanOperator op);

	SDD and(SDD sdd);

	SDD or(SDD sdd);

	SDD xor(SDD sdd);

	SDD not();

	Collection<Element> expansion();

	SDD trimmed();

	int size();

	Formula getFormula();

	boolean eval(Set<Variable> trueLiterals);

	DecompositionSDD nest(InternalVTree vtree);

	/**
	 * For debugging purposes only.
	 */
	void dump();

	void accept(SDDVisitor visitor);

}
