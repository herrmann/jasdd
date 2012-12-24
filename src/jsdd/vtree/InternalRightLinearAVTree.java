package jsdd.vtree;

import java.util.HashSet;
import java.util.Set;

import jsdd.Variable;

/**
 * Special kind of internal algebraic vtree node which supports only variable nodes on the left side.
 * 
 * @author Ricardo Herrmann
 */
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

	@Override
	public Set<Variable> partitionVariables() {
		final HashSet<Variable> vars = new HashSet<Variable>(1);
		vars.add(getLeft().getVariable());
		return vars;
	}

}
