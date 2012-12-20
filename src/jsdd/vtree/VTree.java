package jsdd.vtree;

import java.util.Set;

import util.StringBuildable;

import jsdd.Variable;

public interface VTree extends Tree, StringBuildable {

	Set<Variable> variables();

	boolean isRightLinear();

}
