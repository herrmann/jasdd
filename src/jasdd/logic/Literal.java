package jasdd.logic;

/**
 * A true or false variable.
 *
 * @author Ricardo Herrmann
 */
public class Literal implements Formula {

	private final Variable variable;
	private final boolean sign;

	public Literal(final Literal literal) {
		this.variable = literal.getVariable();
		this.sign = literal.getSign();
	}

	public Literal(final Variable variable, final boolean sign) {
		this.variable = variable;
		this.sign = sign;
	}

	public Literal(final Variable variable) {
		this(variable, true);
	}

	public static Literal from(final int index) {
		if (index == 0) {
			throw new IllegalArgumentException("Cannot directly create literal from index zero");
		}
		return new Literal(new Variable(Math.abs(index)), index > 0);
	}

	public Variable getVariable() {
		return variable;
	}

	public boolean getSign() {
		return sign;
	}

	public Literal opposite() {
		return new Literal(getVariable(), !getSign());
	}

	@Override
	public String toString() {
		return (sign ? "" : "-") + variable.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (sign ? 1231 : 1237);
		result = prime * result
				+ ((variable == null) ? 0 : variable.hashCode());
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
		final Literal other = (Literal) obj;
		if (sign != other.sign) {
			return false;
		}
		if (variable == null) {
			if (other.variable != null) {
				return false;
			}
		} else if (!variable.equals(other.variable)) {
			return false;
		}
		return true;
	}

}
