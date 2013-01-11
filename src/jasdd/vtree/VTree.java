package jasdd.vtree;

import jasdd.logic.Variable;
import jasdd.util.StringBuildable;

import java.util.Set;



/**
 * Super type for regular vtrees.
 * 
 * @author Ricardo Herrmann
 */
public interface VTree extends Tree, StringBuildable {

	Set<Variable> variables();

	boolean isRightLinear();

}
