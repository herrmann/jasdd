package jsdd.vtree;

import java.util.HashSet;
import java.util.Set;

import jsdd.Variable;

public class ValueLeaf implements AVTree, RightLinearAVTree {

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Set<Variable> partitionVariables() {
		return new HashSet<Variable>();
	}

}
