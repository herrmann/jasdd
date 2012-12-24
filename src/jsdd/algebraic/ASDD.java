package jsdd.algebraic;

import jsdd.vtree.AVTree;
import util.StringBuildable;

public interface ASDD<T> extends StringBuildable {

	T evaluate();
	
	AVTree getTree();

	boolean isTerminal();

}
