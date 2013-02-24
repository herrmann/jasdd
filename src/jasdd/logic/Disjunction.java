package jasdd.logic;

import java.util.List;

/**
 * Disjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Disjunction extends AssociativeConnectorFormula {

	public Disjunction(final List<? extends Formula> formulas) {
		super(formulas);
	}

	@Override
	public CharSequence getConnectorString() {
		return "\\/";
	}

	@Override
	public Formula getTrimmingConstant() {
		return new Constant(true);
	}

	@Override
	public Formula getDummyConstant() {
		return new Constant(false);
	}

}
