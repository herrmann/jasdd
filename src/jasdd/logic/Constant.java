package jasdd.logic;

/**
 * A true or false constant.
 *
 * @author Ricardo Herrmann
 */
public class Constant implements TerminalFormula {

	private final boolean sign;

	public Constant(final boolean sign) {
		this.sign = sign;
	}

	public boolean isSign() {
		return sign;
	}

	@Override
	public String toString() {
		return sign ? "T" : "F";
	}

}
