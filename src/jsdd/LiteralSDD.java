package jsdd;

import java.util.ArrayList;
import java.util.Collection;

/**
 * SDD for a single literal.
 * 
 * @author Ricardo Herrmann
 */
public class LiteralSDD extends TerminalSDD {

	private Literal literal;

	public LiteralSDD(final LiteralSDD sdd) {
		this.literal = new Literal(sdd.getLiteral());
	}

	public LiteralSDD(final int index, final boolean sign) {
		this.literal = new Literal(new Variable(index), sign);
	}

	public LiteralSDD(final int index) {
		this.literal = new Literal(new Variable(index), true);
	}

	public LiteralSDD(final Variable variable, final boolean sign) {
		this.literal = new Literal(variable, sign);
	}

	public Literal getLiteral() {
		return literal;
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(literal.toString());
	}

	@Override
	public boolean isTautology() {
		return false;
	}

	@Override
	public boolean isUnsatisfiable() {
		return false;
	}

	@Override
	public boolean isFalse() {
		return false;
	}

	@Override
	public Collection<PairedBox> expansion() {
		final Collection<PairedBox> expansion = new ArrayList<PairedBox>(2);
		expansion.add(new PairedBox(getLiteral(), true));
		expansion.add(new PairedBox(getLiteral().opposite(), false));
		return expansion;
	}

	@Override
	public boolean isConsistent() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literal == null) ? 0 : literal.hashCode());
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
		LiteralSDD other = (LiteralSDD) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!literal.equals(other.literal))
			return false;
		return true;
	}

	@Override
	public SDD apply(final ConstantSDD sdd, final BooleanOperator op) {
		return sdd.apply(this, op);
	}

	@Override
	public SDD apply(final LiteralSDD sdd, final BooleanOperator op) {
		final Literal literal = getLiteral();
		final Literal otherLiteral = sdd.getLiteral();
		if (literal.equals(otherLiteral)) {
			return new LiteralSDD(this);
		} else if (literal.equals(otherLiteral.opposite())) {
			if (op.equals(new AndOperator())) {
				return new ConstantSDD(false);
			}
			if (op.equals(new OrOperator())) {
				return new ConstantSDD(true);
			}
		} else {
			// TODO: Respect and match vtrees
			if (op.equals(new AndOperator())) {
				return new DecompositionSDD(getVTree(), new PairedBox(literal, otherLiteral), new PairedBox(literal.opposite(), false));
			}
			if (op.equals(new OrOperator())) {
				return new DecompositionSDD(getVTree(), new PairedBox(literal, true), new PairedBox(literal.opposite(), otherLiteral));
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SDD apply(final DecompositionSDD sdd, final BooleanOperator op) {
		return sdd.apply(this, op);
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
	}

}
