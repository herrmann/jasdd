package jsdd;

import java.util.HashSet;
import java.util.Set;

/**
 * Leaf node of a variable partition tree.
 * 
 * @author Ricardo Herrmann
 */
public class LeafNode extends VTree {

	private Variable variable;

	public LeafNode(final Variable variable) {
		this.variable = variable;
	}

	public LeafNode(final int index) {
		this(new Variable(index));
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public boolean isRightLinear() {
		return true;
	}

	public Variable getVariable() {
		return variable;
	}

	@Override
	public Set<Variable> variables() {
		final Set<Variable> vars = new HashSet<Variable>();
		vars.add(getVariable());
		return vars;
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(getVariable().toString());
	}

}
