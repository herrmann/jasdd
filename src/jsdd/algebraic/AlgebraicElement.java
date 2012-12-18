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

}
