package jsdd.vtree;

import java.util.HashSet;
import java.util.Set;

import jsdd.Variable;

public class ValueLeaf implements AVTree, RightLinearAVTree {

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Set<Variable> partitionVariables() {
		return new HashSet<Variable>();
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder("VALUE");
	}

}
