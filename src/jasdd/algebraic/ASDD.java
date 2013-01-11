package jasdd.algebraic;

import jasdd.visitor.ASDDVisitor;
import jasdd.vtree.AVTree;
import util.StringBuildable;

/**
 * The type of algebraic SDDs.
 * 
 * @author Ricardo Herrmann
 */
public interface ASDD<T> extends StringBuildable {

	T evaluate();
	
	AVTree getTree();

	boolean isTerminal();

	int size();

	void accept(ASDDVisitor<T> visitor);

}
