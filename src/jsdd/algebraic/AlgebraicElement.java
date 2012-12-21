package jsdd.algebraic;

import jsdd.LiteralSDD;
import jsdd.SDD;
import jsdd.Variable;

public class AlgebraicElement<T> {

	private SDD prime;
	private ASDD<T> sub;

	public AlgebraicElement(final SDD prime, final ASDD<T> sub) {
		this.prime = prime;
		this.sub = sub;
	}

	public AlgebraicElement(final Variable v1, final boolean s1, final ASDD<T> sub) {
		this(new LiteralSDD(v1, s1), sub);
	}

	public AlgebraicElement(final Variable v1, final ASDD<T> sub) {
		this(v1, true, sub);
	}

	public SDD getPrime() {
		return prime;
	}

	public ASDD<T> getSub() {
		return sub;
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

}
