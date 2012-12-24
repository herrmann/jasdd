package jsdd.vtree;

import java.util.Set;

import util.StringBuildable;

import jsdd.Variable;

/**
 * Super type for regular vtrees.
 * 
 * @author Ricardo Herrmann
 */
public interface VTree extends Tree, StringBuildable {

	Set<Variable> variables();

	boolean isRightLinear();

}
