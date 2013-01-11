package jasdd.vtree;

import jasdd.Variable;

import java.util.Set;

import util.StringBuildable;


/**
 * Super type for algebraic and regular vtrees.
 * 
 * @author Ricardo Herrmann
 */
public interface Tree extends StringBuildable {

	boolean isLeaf();

	Set<Variable> partitionVariables();

	Leaf leftmostLeaf();

	Leaf rightmostLeaf();

}
