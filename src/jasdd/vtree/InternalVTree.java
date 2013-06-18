package jasdd.vtree;

import jasdd.logic.Variable;

import java.util.HashSet;
import java.util.Set;


/**
 * An internal node of a variable partitioning tree.
 *
 * @author Ricardo Herrmann
 */
public class InternalVTree extends InternalTree<VTree> implements VTree {

	public InternalVTree(final VTree left, final VTree right) {
		super(left, right);
	}

	public InternalVTree(final Variable left, final Variable right) {
		this(new VariableLeaf(left), new VariableLeaf(right));
	}

	public InternalVTree(final int leftIndex, final int rightIndex) {
		this(new VariableLeaf(leftIndex), new VariableLeaf(rightIndex));
	}

	public InternalVTree(final VTree left, final int rightIndex) {
		this(left, new VariableLeaf(rightIndex));
	}

	public InternalVTree(final int leftIndex, final VTree right) {
		this(new VariableLeaf(leftIndex), right);
	}

	public InternalVTree(final Variable leftVar, final VTree right) {
		this(new VariableLeaf(leftVar.getIndex()), right);
	}

	public InternalVTree(final VTree left, final Variable rightVar) {
		this(left, new VariableLeaf(rightVar.getIndex()));
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public boolean isRightLinear() {
		return getLeft().isLeaf() && getRight().isRightLinear();
	}

	@Override
	public Set<Variable> variables() {
		final Set<Variable> vars = new HashSet<Variable>();
		vars.addAll(getLeft().variables());
		vars.addAll(getRight().variables());
		return vars;
	}

	@Override
	public Set<Variable> partitionVariables() {
		final Set<Variable> vars = getLeft().partitionVariables();
		vars.addAll(getRight().partitionVariables());
		return vars;
	}

	@Override
	public VariableLeaf leftmostLeaf() {
		return (VariableLeaf) getLeft().leftmostLeaf();
	}

	@Override
	public VariableLeaf rightmostLeaf() {
		return (VariableLeaf) getRight().rightmostLeaf();
	}

	/**
	 * Creates a new internal node sharing this node's left and right sub-vtrees
	 * but in swapped order.
	 *
	 * @return an internal node with swapped left and right sub-vtrees
	 */
	public InternalVTree swap() {
		return new InternalVTree(getRight(), getLeft());
	}

	/**
	 * Creates a new internal node corresponding to the structure of the
	 * original vtree after rotating the root node right and sharing sub-vtrees.
	 *
	 * @return an internal node with the vtree rotated right
	 */
	public InternalVTree rotateRight() {
		final VTree sub = getLeft();
		if (sub instanceof Leaf) {
			throw new IllegalArgumentException("The given vtree cannot be rotated further to the right.");
		} else {
			final InternalVTree left = (InternalVTree) sub;
			return new InternalVTree(left.getLeft(), new InternalVTree(left.getRight(), getRight()));
		}
	}

	/**
	 * Creates a new internal node corresponding to the structure of the
	 * original vtree after rotating the root node left and sharing sub-vtrees.
	 *
	 * @return an internal node with the vtree rotated left
	 */
	public InternalVTree rotateLeft() {
		final VTree sub = getRight();
		if (sub instanceof Leaf) {
			throw new IllegalArgumentException("The given vtree cannot be rotated further to the right.");
		} else {
			final InternalVTree right = (InternalVTree) sub;
			return new InternalVTree(new InternalVTree(getLeft(), right.getLeft()), right.getRight());
		}
	}

}
