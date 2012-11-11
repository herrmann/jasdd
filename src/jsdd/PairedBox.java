package jsdd;

import util.StringBuildable;

public class PairedBox implements Sentence, StringBuildable {

	private SDD prime, sub;

	public PairedBox(final SDD prime, final SDD sub) {
		this.prime = prime;
		this.sub = sub;
	}

	public PairedBox(final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		this(new LiteralSDD(v1, s1), new LiteralSDD(v2, s2));
	}

	public PairedBox(final Variable v1, final Variable v2) {
		this(v1, true, v2, true);
	}

	public PairedBox(final Variable v1, final boolean s2) {
		this(v1, true, s2);
	}

	public PairedBox(final boolean s1, final Variable v2) {
		this(s1, v2, true);
	}

	public PairedBox(final Variable v1, final boolean s1, final Variable v2) {
		this(v1, s1, v2, true);
	}

	public PairedBox(final Variable v1, final Variable v2, final boolean s2) {
		this(v1, true, v2, s2);
	}

	public PairedBox(final Variable v1, final boolean s1, final boolean s2) {
		this(new LiteralSDD(v1, s1), new ConstantSDD(s2));
	}

	public PairedBox(final boolean s1, final Variable v2, final boolean s2) {
		this(new ConstantSDD(s1), new LiteralSDD(v2, s2));
	}

	public PairedBox(final boolean s1, final boolean s2) {
		this(new ConstantSDD(s1), new ConstantSDD(s2));
	}

	public PairedBox(final SDD prime, final boolean s2) {
		this(prime, new ConstantSDD(s2));
	}

	public PairedBox(final boolean s1, final SDD sub) {
		this(new ConstantSDD(s1), sub);
	}

	public PairedBox(final SDD prime, final Variable v2, final boolean s2) {
		this(prime, new LiteralSDD(v2, s2));
	}

	public PairedBox(final SDD prime, final Variable v2) {
		this(prime, v2, true);
	}

	public PairedBox(final Variable v1, final boolean s1, final SDD sub) {
		this(new LiteralSDD(v1, s1), sub);
	}

	public PairedBox(final Variable v1, final SDD sub) {
		this(v1, true, sub);
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
		final boolean tautoPrime = getPrime().isTautology();
		final boolean tautoSub = getSub().isTautology();
		if (getPrime().isUnsatisfiable() || getSub().isUnsatisfiable()) {
			sb.append(FALSE);
		} else if (tautoPrime && tautoSub) {
			sb.append(TRUE);
		} else if (!tautoPrime && !tautoSub) {
			sb.append("(");
			sb.append(getPrime().toStringBuilder());
			sb.append(") /\\ {");
			sb.append(getSub().toStringBuilder());
			sb.append("}");
		} else if (tautoPrime) {
			sb.append(getSub().toStringBuilder());
		} else if (tautoSub) {
			sb.append(getPrime().toStringBuilder());
		}
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

}
