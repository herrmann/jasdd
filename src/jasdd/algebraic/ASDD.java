package jasdd.algebraic;

import jasdd.logic.Variable;
import jasdd.util.StringBuildable;
import jasdd.visitor.ASDDVisitor;
import jasdd.vtree.AVTree;

import java.util.Set;

/**
 * The type of algebraic SDDs.
 * 
 * @author Ricardo Herrmann
 */
public interface ASDD<T> extends StringBuildable {

	AVTree getTree();

	boolean isTerminal();

	int size();

	void accept(ASDDVisitor<T> visitor);

	ASDD<T> trimmed();

	Set<T> terminals();

	T eval(Set<Variable> trueLiterals);

}
