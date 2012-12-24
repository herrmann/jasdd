package jsdd.vtree;

import java.util.Set;

import jsdd.Variable;

public interface Tree {

	boolean isLeaf();

	Set<Variable> partitionVariables();

}
