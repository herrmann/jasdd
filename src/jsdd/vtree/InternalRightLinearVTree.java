package jsdd.vtree;

import java.util.HashSet;
import java.util.Set;

import jsdd.Variable;


public class InternalRightLinearVTree extends InternalTree<RightLinearVTree> implements RightLinearVTree {

	public InternalRightLinearVTree(final VTree left, final RightLinearVTree right) {
		super(left, right);
	}

	@Override
	public VariableLeaf getLeft() {
		return null;
	}

	@Override
	public Set<Variable> variables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRightLinear() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public StringBuilder toStringBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Variable> partitionVariables() {
		final HashSet<Variable> vars = new HashSet<Variable>(1);
		vars.add(getLeft().getVariable());
		return vars;
	}

}
