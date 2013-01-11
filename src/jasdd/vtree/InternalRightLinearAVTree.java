package jasdd.vtree;

import jasdd.logic.Variable;

import java.util.HashSet;
import java.util.Set;


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
		return (VariableLeaf) super.getLeft();
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

	@Override
	public VariableLeaf leftmostLeaf() {
		return getLeft();
	}

	@Override
	public ValueLeaf rightmostLeaf() {
		return (ValueLeaf) getRight().rightmostLeaf();
	}

}
