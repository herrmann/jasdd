package jasdd.logic;

import java.util.List;

/**
 * Disjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Disjunction extends AssociativeConnectorFormula {

	public Disjunction(final List<Formula> formulas) {
		super(formulas);
	}

	@Override
	public CharSequence getConnectorString() {
		return "\\/";
	}

}
