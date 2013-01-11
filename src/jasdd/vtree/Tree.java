package jasdd.vtree;

import jasdd.logic.Variable;
import jasdd.util.StringBuildable;

import java.util.Set;



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
