package jsdd.vtree;

import jsdd.Variable;

public class InternalAVTree extends InternalTree<AVTree> implements AVTree {

	public InternalAVTree(final VTree left, final AVTree right) {
		super(left, right);
	}

	public InternalAVTree(final VTree left) {
		this(left, new ValueLeaf());
	}

	public InternalAVTree(final Variable var, final AVTree right) {
		this(new VariableLeaf(var), right);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
