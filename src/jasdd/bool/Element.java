package jasdd.bool;

import jasdd.JASDD;
import jasdd.logic.Conjunction;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.Sentence;
import jasdd.logic.Variable;
import jasdd.util.StringBuildable;
import jasdd.visitor.SDDVisitor;

import java.util.Arrays;
import java.util.Set;

/**
 * A pair of SDDs representing one element of a decomposition.
 *
 * @author Ricardo Herrmann
 */
public class Element implements Sentence, StringBuildable, Comparable<Element> {

	private final SDD prime, sub;

	/* package */ Element(final SDD prime, final SDD sub) {
		this.prime = prime;
		this.sub = sub;
	}

	private Element(final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		this(JASDD.createLiteral(v1, s1), JASDD.createLiteral(v2, s2));
	}

	private Element(final Variable v1, final Variable v2) {
		this(v1, true, v2, true);
	}

	private Element(final Variable v1, final boolean s2) {
		this(v1, true, s2);
	}

	private Element(final boolean s1, final Variable v2) {
		this(s1, v2, true);
	}

	private Element(final boolean s1, final Literal l2) {
		this(s1, l2.getVariable(), l2.getSign());
	}

	private Element(final Variable v1, final boolean s1, final Variable v2) {
		this(v1, s1, v2, true);
	}

	private Element(final Variable v1, final Variable v2, final boolean s2) {
		this(v1, true, v2, s2);
	}

	private Element(final Variable v1, final boolean s1, final boolean s2) {
		this(JASDD.createLiteral(v1, s1), JASDD.createConstant(s2));
	}

	private Element(final boolean s1, final Variable v2, final boolean s2) {
		this(JASDD.createConstant(s1), JASDD.createLiteral(v2, s2));
	}

	private Element(final boolean s1, final boolean s2) {
		this(JASDD.createConstant(s1), JASDD.createConstant(s2));
	}

	private Element(final SDD prime, final boolean s2) {
		this(prime, JASDD.createConstant(s2));
	}

	private Element(final boolean s1, final SDD sub) {
		this(JASDD.createConstant(s1), sub);
	}

	private Element(final SDD prime, final Variable v2, final boolean s2) {
		this(prime, JASDD.createLiteral(v2, s2));
	}

	private Element(final SDD prime, final Variable v2) {
		this(prime, v2, true);
	}

	private Element(final Variable v1, final boolean s1, final SDD sub) {
		this(JASDD.createLiteral(v1, s1), sub);
	}

	private Element(final Variable v1, final SDD sub) {
		this(v1, true, sub);
	}

	private Element(final Literal l1, final boolean s2) {
		this(l1.getVariable(), l1.getSign(), s2);
	}

	private Element(final Literal l1, final Literal l2) {
		this(l1.getVariable(), l1.getSign(), l2.getVariable(), l2.getSign());
	}

	public SDD getPrime() {
		return prime;
	}

	public SDD getSub() {
		return sub;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public StringBuilder toStringBuilder() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(").append(getPrime().toStringBuilder()).append(" /\\ ").append(getSub().toStringBuilder()).append(")");
		return sb;
	}

	@Override
	public boolean isTautology() {
		return getPrime().isTautology() && getSub().isTautology();
	}

	@Override
	public boolean isUnsatisfiable() {
		return getPrime().isUnsatisfiable() || getSub().isUnsatisfiable();
	}

	@Override
	public boolean isConsistent() {
		// TODO Auto-generated method stub
		return false;
	}

	private Integer hashCode;

	@Override
	public int hashCode() {
		if (hashCode == null) {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((this.prime == null) ? 0 : this.prime.hashCode());
			result = prime * result + ((sub == null) ? 0 : sub.hashCode());
			hashCode = result;
		}
		return hashCode;
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
		final Element other = (Element) obj;
		if (prime == null) {
			if (other.prime != null) {
				return false;
			}
		} else if (!prime.equals(other.prime)) {
			return false;
		}
		if (sub == null) {
			if (other.sub != null) {
				return false;
			}
		} else if (!sub.equals(other.sub)) {
			return false;
		}
		return true;
	}

	public Element trimmed() {
		return JASDD.createElement(getPrime().trimmed(), getSub().trimmed());
	}

	public void accept(final SDDVisitor visitor) {
		if (visitor.visit(this)) {
			getPrime().accept(visitor);
			getSub().accept(visitor);
			visitor.postVisit(this);
		}
	}

	public Formula getFormula() {
		return new Conjunction(Arrays.asList(getPrime().getFormula(), getSub().getFormula()));
	}

	public boolean eval(final Set<Variable> trueLiterals) {
		return getPrime().eval(trueLiterals) && getSub().eval(trueLiterals);
	}

	public Element not() {
		return JASDD.createElement(getPrime(), getSub().not());
	}

	@Override
	public int compareTo(final Element other) {
		final int cmp = getPrime().compareTo(other.getPrime());
		if (cmp != 0) {
			return cmp;
		} else {
			return getSub().compareTo(other.getSub());
		}
	}

}
