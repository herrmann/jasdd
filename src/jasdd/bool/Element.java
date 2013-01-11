package jasdd.bool;

import jasdd.logic.Literal;
import jasdd.logic.Sentence;
import jasdd.logic.Variable;
import jasdd.util.StringBuildable;
import jasdd.visitor.SDDVisitor;

/**
 * A pair of SDDs representing one element of a decomposition.
 * 
 * @author Ricardo Herrmann
 */
public class Element implements Sentence, StringBuildable {

	private SDD prime, sub;

	public Element(final SDD prime, final SDD sub) {
		this.prime = prime;
		this.sub = sub;
	}

	public Element(final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		this(new LiteralSDD(v1, s1), new LiteralSDD(v2, s2));
	}

	public Element(final Variable v1, final Variable v2) {
		this(v1, true, v2, true);
	}

	public Element(final Variable v1, final boolean s2) {
		this(v1, true, s2);
	}

	public Element(final boolean s1, final Variable v2) {
		this(s1, v2, true);
	}

	public Element(final boolean s1, final Literal l2) {
		this(s1, l2.getVariable(), l2.getSign());
	}

	public Element(final Variable v1, final boolean s1, final Variable v2) {
		this(v1, s1, v2, true);
	}

	public Element(final Variable v1, final Variable v2, final boolean s2) {
		this(v1, true, v2, s2);
	}

	public Element(final Variable v1, final boolean s1, final boolean s2) {
		this(new LiteralSDD(v1, s1), new ConstantSDD(s2));
	}

	public Element(final boolean s1, final Variable v2, final boolean s2) {
		this(new ConstantSDD(s1), new LiteralSDD(v2, s2));
	}

	public Element(final boolean s1, final boolean s2) {
		this(new ConstantSDD(s1), new ConstantSDD(s2));
	}

	public Element(final SDD prime, final boolean s2) {
		this(prime, new ConstantSDD(s2));
	}

	public Element(final boolean s1, final SDD sub) {
		this(new ConstantSDD(s1), sub);
	}

	public Element(final SDD prime, final Variable v2, final boolean s2) {
		this(prime, new LiteralSDD(v2, s2));
	}

	public Element(final SDD prime, final Variable v2) {
		this(prime, v2, true);
	}

	public Element(final Variable v1, final boolean s1, final SDD sub) {
		this(new LiteralSDD(v1, s1), sub);
	}

	public Element(final Variable v1, final SDD sub) {
		this(v1, true, sub);
	}

	public Element(final Literal l1, boolean s2) {
		this(l1.getVariable(), l1.getSign(), s2);
	}

	public Element(final Literal l1, final Literal l2) {
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Element other = (Element) obj;
		if (prime == null) {
			if (other.prime != null)
				return false;
		} else if (!prime.equals(other.prime))
			return false;
		if (sub == null) {
			if (other.sub != null)
				return false;
		} else if (!sub.equals(other.sub))
			return false;
		return true;
	}

	public Element trimmed() {
		return new Element(getPrime().trimmed(), getSub().trimmed());
	}

	public static Element[] shannon(final Variable v, final Variable v1, final Variable v2, final boolean s2) {
		return shannon(v, v1, true, v2, s2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2) {
		return shannon(v, v1, s1, v2, true);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final Variable v2) {
		return shannon(v, v1, true, v2, true);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = new Element(v, true, v1, s1);
		elems[1] = new Element(v, false, v2, s2);
		return elems;
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s2) {
		return shannon(v, v1, true, s2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s1, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = new Element(v, true, v1, s1);
		elems[1] = new Element(v, false, s2);
		return elems;
	}

	public static Element[] shannon(final Variable v, final boolean s1, final Variable v2) {
		return shannon(v, s1, v2, true);
	}

	public static Element[] shannon(final Variable v, final boolean s1, final Variable v2, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = new Element(v, true, s1);
		elems[1] = new Element(v, false, v2, s2);
		return elems;
	}

	public static Element[] shannon(final Variable v, final boolean s1, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = new Element(v, true, s1);
		elems[1] = new Element(v, false, s2);
		return elems;
	}

	public void accept(final SDDVisitor visitor) {
		if (visitor.visit(this)) {
			getPrime().accept(visitor);
			getSub().accept(visitor);
			visitor.postVisit(this);
		}
	}

}
