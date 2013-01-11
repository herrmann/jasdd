package jasdd.vtree;

import jasdd.Variable;

import java.util.Set;

import util.StringBuildable;


/**
 * Super type for regular vtrees.
 * 
 * @author Ricardo Herrmann
 */
public interface VTree extends Tree, StringBuildable {

	Set<Variable> variables();

	boolean isRightLinear();

}
