package jasdd.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Conjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Conjunction extends AssociativeConnectorFormula {

	public static Formula from(final Collection<? extends Formula> formulas) {
		final Set<Formula> unique = new HashSet<Formula>(formulas);
		if (unique.size() == 1) {
			return unique.iterator().next();
		} else {
			return new Conjunction(unique);
		}
	}

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

	@Override
	public Set<Set<Literal>> toCnf() {
		final Set<Set<Literal>> cnf = new HashSet<Set<Literal>>();
		for (final Formula f : getFormulas()) {
			final Set<Set<Literal>> conj = f.toCnf();
			for (final Set<Literal> disj : conj) {
				cnf.add(disj);
			}
		}
		return cnf;
	}

}
