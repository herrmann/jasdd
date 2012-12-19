package jsdd.vtree;

import java.util.HashSet;
import java.util.Set;

import jsdd.Variable;

/**
 * An internal node of a variable partitioning tree.
 *  
 * @author Ricardo Herrmann
 */
public class InternalVTree extends InternalTree<VTree> implements VTree {

	private VTree left, right;

	public InternalVTree(final Variable left, final Variable right) {
		this.left = new VariableLeaf(left);
		this.right = new VariableLeaf(right);
	}

	public InternalVTree(final VTree left, final VTree right) {
		this.left = left;
		this.right = right;
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

	public VTree getLeft() {
		return left;
	}

	public VTree getRight() {
		return right;
	}

	@Override
	public Set<Variable> variables() {
		final Set<Variable> vars = new HashSet<Variable>();
		vars.addAll(getLeft().variables());
		vars.addAll(getRight().variables());
		return vars;
	}

	@Override
	public StringBuilder toStringBuilder() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(getLeft().toStringBuilder());
		sb.append(", ");
		sb.append(getRight().toStringBuilder());
		sb.append(")");
		return sb;
	}

}
