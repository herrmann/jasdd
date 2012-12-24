package jsdd.vtree;

import java.util.Set;

import jsdd.Variable;

public class InternalAVTree extends InternalTree<AVTree> implements AVTree {

	public InternalAVTree(final VTree left, final AVTree right) {
		super(left, right);
	}

	public InternalAVTree(final VTree left) {
		this(left, new ValueLeaf());
	}

	public InternalAVTree(final Variable var) {
		this(var, new ValueLeaf());
	}

	public InternalAVTree(final Variable var, final AVTree right) {
		this(new VariableLeaf(var), right);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public Set<Variable> partitionVariables() {
		return getLeft().partitionVariables();
	}

}
