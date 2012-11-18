package jsdd;

/**
 * Base class with utility methods for all kinds of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public abstract class AbstractSDD implements SDD {

	private boolean prime;
	private Element parent;

	@Override
	public Element getParent() {
		return parent;
	}

	protected boolean hasParent() {
		return parent != null;
	}

	/* package */ void setParent(final Element parent) {
		this.parent = parent;
	}

	@Override
	public boolean isPrime() {
		return prime;
	}

	/* package */ void markPrime() {
		prime = true;
	}

	/* package */ void markSub() {
		prime = false;
	}

	@Override
	public boolean isSub() {
		return hasParent() && !prime;
	}

	public static SDD decomposition(final InternalNode node, final Element... elements) {
		return new DecompositionSDD(node, elements);
	}

	public boolean isTrivial() {
		return isTautology() || isUnsatisfiable();
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public SDD apply(final SDD sdd, final BooleanOperator op) {
		if (sdd instanceof ConstantSDD) {
			return apply((ConstantSDD) sdd, op);
		}
		else if (sdd instanceof LiteralSDD) {
			return apply((LiteralSDD) sdd, op);
		}
		else if (sdd instanceof DecompositionSDD) {
			return apply((DecompositionSDD) sdd, op);
		}
		return null;
	}

	@Override
	public SDD and(final SDD sdd) {
		return apply(sdd, new AndOperator());
	}

	@Override
	public SDD or(final SDD sdd) {
		return apply(sdd, new OrOperator());
	}

}
