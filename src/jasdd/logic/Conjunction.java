package jasdd.logic;

import java.util.List;

/**
 * Conjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Conjunction extends AssociativeConnectorFormula {

	public Conjunction(final List<Formula> formulas) {
		super(formulas);
	}

	@Override
	public CharSequence getConnectorString() {
		return "/\\";
	}

}
