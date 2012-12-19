package jsdd.algebraic;

import jsdd.vtree.AVTree;

public interface ASDD<T> {

	T evaluate();
	
	AVTree getTree();

	boolean isTerminal();

}
