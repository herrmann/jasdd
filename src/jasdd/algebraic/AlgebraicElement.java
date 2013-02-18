package jasdd.algebraic;

import jasdd.bool.SDD;
import jasdd.bool.CachingSDDFactory;
import jasdd.logic.Variable;
import jasdd.util.StringBuildable;
import jasdd.visitor.ASDDVisitor;

/**
 * One of the composing elements (paired boxes) of an algebraic SDD.
 * 
 * @author Ricardo Herrmann
 */
public class AlgebraicElement<T> implements StringBuildable {

	private SDD prime;
	private ASDD<T> sub;

	/* package */ AlgebraicElement(final SDD prime, final ASDD<T> sub) {
		this.prime = prime;
		this.sub = sub;
	}

	private AlgebraicElement(final Variable v1, final boolean s1, final ASDD<T> sub) {
		this(CachingSDDFactory.getInstance().createLiteral(v1, s1), sub);
	}

	private AlgebraicElement(final Variable v1, final ASDD<T> sub) {
		this(v1, true, sub);
	}

	public SDD getPrime() {
		return prime;
	}

	public ASDD<T> getSub() {
		return sub;
	}

	private static <T> AlgebraicElement<T>[] shannon(final Variable v, final ASDD<T> high, final ASDD<T> low) {
		@SuppressWarnings("unchecked")
		final AlgebraicElement<T>[] elems = new AlgebraicElement[2];
		elems[0] = new AlgebraicElement<T>(v, high);
		elems[1] = new AlgebraicElement<T>(v, false, low);
		return elems;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.prime == null) ? 0 : this.prime.hashCode());
		result = prime * result + ((sub == null) ? 0 : sub.hashCode());
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
		@SuppressWarnings("unchecked")
		AlgebraicElement<T> other = (AlgebraicElement<T>) obj;
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

	public void accept(final ASDDVisitor<T> visitor) {
		if (visitor.visit(this)) {
			getPrime().accept(visitor);
			getSub().accept(visitor);
			visitor.postVisit(this);
		}
	}

	public AlgebraicElement<T> trimmed() {
		return new AlgebraicElement<T>(getPrime().trimmed(), getSub().trimmed());
	}

}
