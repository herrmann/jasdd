package jasdd;

/**
 * A true or false variable.
 * 
 * @author Ricardo Herrmann
 */
public class Literal {

	private Variable variable;
	private boolean sign;
	
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Literal other = (Literal) obj;
		if (sign != other.sign)
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

}
