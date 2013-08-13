package jasdd.logic;

/**
 * Representation of a logical boolean variable.
 * 
 * @author Ricardo Herrmann
 */
public class Variable implements Comparable<Variable> {

	private int index;

	public Variable(final int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return Integer.toString(getIndex());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
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
		Variable other = (Variable) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public int compareTo(final Variable other) {
		return Integer.valueOf(getIndex()).compareTo(other.getIndex());
	}

}
