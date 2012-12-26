package jsdd.algebraic;

import jsdd.vtree.AVTree;
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

}
