package jasdd.vtree;

import jasdd.logic.Variable;

import java.util.Set;


/**
 * Internal node of an algebraic vtree.
 *
 * @author Ricardo Herrmann
 */
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

	@Override
	public VariableLeaf leftmostLeaf() {
		return (VariableLeaf) getLeft().leftmostLeaf();
	}

	@Override
	public ValueLeaf rightmostLeaf() {
		return (ValueLeaf) getRight().rightmostLeaf();
	}

	@Override
	public boolean canRotateLeft() {
		return !getRight().isLeaf();
	}

	@Override
	public boolean canRotateRight() {
		return !getLeft().isLeaf();
	}

}
