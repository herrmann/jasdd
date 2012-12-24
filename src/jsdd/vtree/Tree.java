package jsdd.vtree;

import java.util.Set;

import util.StringBuildable;

import jsdd.Variable;

public interface Tree extends StringBuildable {

	boolean isLeaf();

	Set<Variable> partitionVariables();

}
