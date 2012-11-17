package jsdd;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import util.StringBuildable;

/**
 * A pair of SDDs representing one element of a decomposition.
 * 
 * @author Ricardo Herrmann
 */
public class PairedBox implements Sentence, StringBuildable {

	private Set<DecompositionSDD> parents = new HashSet<DecompositionSDD>();
	private SDD prime, sub;

	public PairedBox(final SDD prime, final SDD sub) {
		this.prime = prime;
		this.sub = sub;
		((AbstractSDD) prime).markPrime();
		((AbstractSDD) prime).setParent(this);
		((AbstractSDD) sub).markSub();
		((AbstractSDD) sub).setParent(this);
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

	public PairedBox(final boolean s1, final Literal l2) {
		this(s1, l2.getVariable(), l2.getSign());
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

	public PairedBox(final Literal l1, boolean s2) {
		this(l1.getVariable(), l1.getSign(), s2);
	}

	public PairedBox(final Literal l1, final Literal l2) {
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

	@Override
	public boolean isConsistent() {
		// TODO Auto-generated method stub
		return false;
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
		PairedBox other = (PairedBox) obj;
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

	public Collection<DecompositionSDD> getParents() {
		return Collections.unmodifiableCollection(parents);
	}

	/* package */ void addParent(final DecompositionSDD parent) {
		parents.add(parent);
	}

	public VTree getVTree() {
		// TODO: check if paired boxes' parent decompositions all use the same vtree.
		return getParents().iterator().next().getVTree();
	}

}
