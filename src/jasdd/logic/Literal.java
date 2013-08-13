package jasdd.logic;

import java.util.HashSet;
import java.util.Set;

/**
 * A true or false variable.
 *
 * @author Ricardo Herrmann
 */
public class Literal extends TerminalFormula implements Comparable<Literal> {

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

	@Override
	public Set<Set<Literal>> toCnf() {
		final HashSet<Set<Literal>> conjunction = new HashSet<Set<Literal>>(); 
		final HashSet<Literal> disjunction = new HashSet<Literal>();
		disjunction.add(this);
		conjunction.add(disjunction);
		return conjunction;
	}

	@Override
	public int compareTo(final Literal other) {
		final int cmp = getVariable().compareTo(other.getVariable());
		if (cmp != 0) {
			return cmp;
		} else {
			return new Constant(getSign()).compareTo(new Constant(other.getSign()));
		}
	}

}
