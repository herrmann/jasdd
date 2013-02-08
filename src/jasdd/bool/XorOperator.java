package jasdd.bool;

import jasdd.logic.BooleanOperator;

/**
 * Exclusive-or.
 *  
 * @author Ricardo Herrmann
 */
public class XorOperator implements BooleanOperator {

	@Override
	public boolean apply(final boolean s1, final boolean s2) {
		return s1 ^ s2;
	}

}
