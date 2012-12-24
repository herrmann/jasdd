package jsdd.vtree;

import util.StringBuildable;

public interface Internal<T> extends StringBuildable {

	VTree getLeft();
	
	T getRight();

}
