package jsdd.vtree;

import java.util.HashSet;
import java.util.Set;

import util.StringBuildable;

import jsdd.Variable;

/**
 * Leaf node of a variable partition tree.
 * 
 * @author Ricardo Herrmann
 */
public class VariableLeaf implements VTree, RightLinearVTree {

	private Variable variable;

	public VariableLeaf(final Variable variable) {
		this.variable = variable;
	}

	public VariableLeaf(final int index) {
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

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((variable == null) ? 0 : variable.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableLeaf other = (VariableLeaf) obj;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

	@Override
	public Set<Variable> partitionVariables() {
		final HashSet<Variable> vars = new HashSet<Variable>(1);
		vars.add(getVariable());
		return vars;
	}

}
