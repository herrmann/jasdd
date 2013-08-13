package jasdd.logic;

import java.util.Set;

/**
 * A true or false constant.
 *
 * @author Ricardo Herrmann
 */
public class Constant extends TerminalFormula implements Comparable<Constant> {

	private final boolean sign;

	public Constant(final boolean sign) {
		this.sign = sign;
	}

	public boolean isSign() {
		return sign;
	}

	@Override
	public String toString() {
		return sign ? "T" : "F";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (sign ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Constant other = (Constant) obj;
		if (sign != other.sign) {
			return false;
		}
		return true;
	}

	@Override
	public Set<Set<Literal>> toCnf() {
		throw new IllegalStateException("Trivial (constant) boolean functions are not allowed in CNF conversions.");
	}

	@Override
	public int compareTo(final Constant other) {
		if (isSign()) {
			if (other.isSign()) {
				return 0;
			} else {
				return 1;
			}
		} else {
			if (other.isSign()) {
				return -1;
			} else {
				return 0;
			}
		}
	}

}
