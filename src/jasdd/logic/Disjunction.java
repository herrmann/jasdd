package jasdd.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 * Disjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Disjunction extends AssociativeConnectorFormula {

	public static Formula from(final Collection<? extends Formula> formulas) {
		final Set<Formula> unique = new HashSet<Formula>(formulas);
		if (unique.size() == 1) {
			return unique.iterator().next();
		} else {
			return new Disjunction(unique);
		}
	}

	public Disjunction(final Collection<? extends Formula> formulas) {
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
