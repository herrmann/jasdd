package jsdd;

/**
 * Logical AND operator over SDDs.
 * 
 * @author Ricardo Herrmann
 */
public class AndOperator implements BooleanOperator {

	@Override
	public SDD apply(final ConstantSDD s1, final ConstantSDD s2) {
		return new ConstantSDD(s1.getSign() && s2.getSign());
	}

	@Override
	public SDD apply(final ConstantSDD s1, final LiteralSDD s2) {
		if (s1.getSign()) {
			return new LiteralSDD(s2);
		} else {
			return new ConstantSDD(false);
		}
	}

	@Override
	public SDD apply(final LiteralSDD s1, final ConstantSDD s2) {
		return apply(s2, s1);
	}

}
