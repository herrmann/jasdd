package jasdd.bool;

import jasdd.logic.BooleanOperator;

/**
 * Logical AND operator over SDDs.
 * 
 * @author Ricardo Herrmann
 */
public class AndOperator implements BooleanOperator {

	public SDD apply(final ConstantSDD s1, final ConstantSDD s2) {
		return SDDFactory.getInstance().createConstant(s1.getSign() && s2.getSign());
	}

	public SDD apply(final ConstantSDD s1, final LiteralSDD s2) {
		final SDDFactory factory = SDDFactory.getInstance();
		if (s1.getSign()) {
			return factory.createLiteral(s2);
		} else {
			return factory.createFalse();
		}
	}

	public SDD apply(final LiteralSDD s1, final ConstantSDD s2) {
		return apply(s2, s1);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj != null && obj.getClass().equals(getClass());
	}

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean apply(boolean s1, boolean s2) {
		return s1 && s2;
	}

	@Override
	public String toString() {
		return "AND";
	}

}
