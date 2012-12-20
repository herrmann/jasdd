package jsdd.vtree;


public class InternalRightLinearAVTree extends InternalTree<RightLinearAVTree> implements RightLinearAVTree {

	public InternalRightLinearAVTree(final VTree left, final RightLinearAVTree right) {
		super(left, right);
	}

	@Override
	public VariableLeaf getLeft() {
		return null;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
