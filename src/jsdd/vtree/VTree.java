package jsdd.vtree;

import java.util.Set;

import util.StringBuildable;

import jsdd.Variable;

public interface VTree extends StringBuildable{

	Set<Variable> variables();

	boolean isLeaf();

	boolean isRightLinear();

}
