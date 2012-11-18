package jsdd;

import java.util.HashSet;
import java.util.Set;

/**
 * An internal node of a variable partitioning tree.
 *  
 * @author Ricardo Herrmann
 */
public class InternalNode extends VTree {

	private VTree left, right;

	public InternalNode(final Variable left, final Variable right) {
		this.left = new LeafNode(left);
		this.right = new LeafNode(right);
	}

	public InternalNode(final VTree left, final VTree right) {
		this.left = left;
		this.right = right;
	}

	public InternalNode(final int leftIndex, final int rightIndex) {
		this(new LeafNode(leftIndex), new LeafNode(rightIndex));
	}

	public InternalNode(final VTree left, final int rightIndex) {
		this(left, new LeafNode(rightIndex));
	}

	public InternalNode(final int leftIndex, final VTree right) {
		this(new LeafNode(leftIndex), right);
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
