package jasdd.vtree;

import jasdd.logic.Variable;

import java.util.HashSet;
import java.util.Set;


/**
 * Special kind of internal vtree node which supports only variable nodes on the left side.
 *
 * @author Ricardo Herrmann
 */
@Deprecated
public class InternalRightLinearVTree extends InternalTree<RightLinearVTree> implements RightLinearVTree {

	public InternalRightLinearVTree(final VTree left, final RightLinearVTree right) {
		super(left, right);
	}

	@Override
	public InternalRightLinearVTree build(final VTree left, final RightLinearVTree right) {
		return new InternalRightLinearVTree(left, right);
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

	@Override
	public VariableLeaf leftmostLeaf() {
		return getLeft();
	}

	@Override
	public VariableLeaf rightmostLeaf() {
		return (VariableLeaf) getRight().rightmostLeaf();
	}

	@Override
	public boolean canRotateLeft() {
		return !getRight().isLeaf();
	}

	@Override
	public boolean canRotateRight() {
		return false;
	}

	@Override
	public Tree rotateLeft() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tree rotateRight() {
		// TODO Auto-generated method stub
		return null;
	}

}
