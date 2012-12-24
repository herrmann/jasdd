package jsdd.vtree;

import java.util.Set;

import util.StringBuildable;

import jsdd.Variable;

/**
 * Super type for algebraic and regular vtrees.
 * 
 * @author Ricardo Herrmann
 */
public interface Tree extends StringBuildable {

	boolean isLeaf();

	Set<Variable> partitionVariables();

}
