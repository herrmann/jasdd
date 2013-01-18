package jasdd.vtree;

import jasdd.util.StringBuildable;

/**
 * Auxiliary super type for internal vtree (algebraic or not) nodes.
 * 
 * @author Ricardo Herrmann
 */
public interface Internal<T> extends StringBuildable {

	VTree getLeft();
	
	T getRight();

}