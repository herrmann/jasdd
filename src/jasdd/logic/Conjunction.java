package jasdd.logic;

import java.util.Collection;

/**
 * Conjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Conjunction extends AssociativeConnectorFormula {

	public Conjunction(final Collection<? extends Formula> formulas) {
		super(formulas);
	}

	@Override
	public CharSequence getConnectorString() {
		return "/\\";
	}

	@Override
	public Formula getTrimmingConstant() {
		return new Constant(false);
	}

	@Override
	public Formula getDummyConstant() {
		return new Constant(true);
	}

}
