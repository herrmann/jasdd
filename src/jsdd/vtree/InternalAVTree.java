package jsdd.vtree;

public class InternalAVTree extends InternalTree<AVTree> implements AVTree {

	public InternalAVTree(final VTree left, final AVTree right) {
		super(left, right);
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
