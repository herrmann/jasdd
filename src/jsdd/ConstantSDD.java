package jsdd;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Constant SDD, which is either true or false.
 * 
 * @author Ricardo Herrmann
 */
public class ConstantSDD extends TerminalSDD {

	private boolean sign;

	public ConstantSDD(final boolean sign) {
		this.sign = sign;
	}

	public boolean getSign() {
		return sign;
	}

	@Override
	public boolean isTautology() {
		return sign;
	}

	@Override
	public boolean isUnsatisfiable() {
		return !sign;
	}

	@Override
	public boolean isFalse() {
		return isUnsatisfiable();
	}

	@Override
	public SDD apply(final ConstantSDD sdd, final BooleanOperator op) {
		return new ConstantSDD(op.apply(getSign(), sdd.getSign()));
	}

	@Override
	public SDD apply(final LiteralSDD sdd, final BooleanOperator op) {
		if (op.equals(new AndOperator())) {
			if (getSign()) {
				return new LiteralSDD(sdd);
			} else {
				return new ConstantSDD(false);
			}
		}
		return null;
	}

	@Override
	public SDD apply(final DecompositionSDD sdd, final BooleanOperator op) {
		if (op.equals(new AndOperator())) {
			if (getSign()) {
				return new DecompositionSDD(sdd);
			} else {
				return new ConstantSDD(false);
			}
		}
		return null;
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(sign ? TRUE : FALSE);
	}

	@Override
	public Collection<PairedBox> expansion() {
		final Collection<PairedBox> expansion = new ArrayList<PairedBox>(1);
		expansion.add(new PairedBox(true, getSign()));
		return expansion;
	}

	@Override
	public boolean isConsistent() {
		return getSign();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (sign ? 1231 : 1237);
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
		ConstantSDD other = (ConstantSDD) obj;
		if (sign != other.sign)
			return false;
		return true;
	}

}
