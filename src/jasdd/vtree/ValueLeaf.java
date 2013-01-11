package jasdd.vtree;

import jasdd.Variable;

import java.util.HashSet;
import java.util.Set;


/**
 * The special rightmost node of an algebraic vtree. 
 * 
 * @author Ricardo Herrmann
 */
public class ValueLeaf implements AVTree, RightLinearAVTree, Leaf {

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

	@Override
	public ValueLeaf leftmostLeaf() {
		return this;
	}

	@Override
	public ValueLeaf rightmostLeaf() {
		return this;
	}

}
