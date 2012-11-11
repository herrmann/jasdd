package jsdd;

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
	public SDD and(final SDD sdd) {
		if (getSign()) {
			return sdd; // TODO: clone
		} else {
			return new ConstantSDD(false);
		}
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(sign ? TRUE : FALSE);
	}

}
