package jsdd;

public class LiteralSDD extends TerminalSDD {

	private Literal literal;

	public LiteralSDD(final Variable variable, final boolean sign) {
		this.literal = new Literal(variable, sign);
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
	public SDD and(final SDD sdd) {
		// TODO Auto-generated method stub
		return null;
	}

}
