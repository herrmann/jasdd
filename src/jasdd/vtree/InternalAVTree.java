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

	/**
	 * Creates a new internal node corresponding to the structure of the
	 * original avtree after rotating the root node right and sharing sub-vtrees.
	 *
	 * @return an internal node with the avtree rotated right
	 */
	@Override
	public InternalAVTree rotateRight() {
		if (!canRotateRight()) {
			throw new IllegalArgumentException("The given vtree cannot be rotated further to the right.");
		} else {
			final InternalVTree left = (InternalVTree) getLeft();
			return new InternalAVTree(left.getLeft(), new InternalAVTree(left.getRight(), getRight()));
		}
	}

	/**
	 * Creates a new internal node corresponding to the structure of the
	 * original avtree after rotating the root node left and sharing sub-vtrees.
	 *
	 * @return an internal node with the avtree rotated left
	 */
	@Override
	public InternalAVTree rotateLeft() {
		if (!canRotateLeft()) {
			throw new IllegalArgumentException("The given vtree cannot be rotated further to the right.");
		} else {
			final InternalAVTree right = (InternalAVTree) getRight();
			return new InternalAVTree(new InternalVTree(getLeft(), right.getLeft()), right.getRight());
		}
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
