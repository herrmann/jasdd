package jasdd.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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

	@Override
	public Set<Set<Literal>> toCnf() {
		final Set<Formula> formulas = getFormulas();
		final Iterator<Formula> iter = formulas.iterator();
		final Formula p = iter.next();
		Set<Set<Literal>> pCnf = p.toCnf();
		while (iter.hasNext()) {
			final Formula q = iter.next();
			final Set<Set<Literal>> qCnf = q.toCnf();
			final HashSet<Set<Literal>> rCnf = new HashSet<Set<Literal>>();
			for (final Set<Literal> pDisj : pCnf) {
				for (final Set<Literal> qDisj : qCnf) {
					final Set<Literal> rDisj = new HashSet<Literal>();
					rDisj.addAll(pDisj);
					rDisj.addAll(qDisj);
					rCnf.add(rDisj);
				}
			}
			pCnf = rCnf;
		}
		return pCnf;
	}

}
