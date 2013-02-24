package jasdd.logic;

/**
 * A true or false constant.
 *
 * @author Ricardo Herrmann
 */
public class Constant extends TerminalFormula {

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

}
