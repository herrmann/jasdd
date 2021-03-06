package jasdd.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Template for formulas composed of a list of sub-formulas joined by a specific
 * associative boolean connector.
 *
 * @author Ricardo Herrmann
 */
public abstract class AssociativeConnectorFormula implements Formula {

	private final Set<Formula> formulas;

	public AssociativeConnectorFormula(final Collection<? extends Formula> formulas) {
		final Set<Formula> set = new HashSet<Formula>(formulas);
		if (set.size() < 2) {
			throw new IllegalArgumentException("Cannot construct connector formula of less than two formulas");
		}
		this.formulas = Collections.unmodifiableSet(set);
	}

	public Set<Formula> getFormulas() {
		return formulas;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		final boolean single = formulas.size() == 1;
		for (final Formula formula : formulas) {
			final boolean terminal = formula instanceof TerminalFormula;
			final boolean useBraces = !terminal && !single;
			if (!first) {
				sb.append(" ").append(getConnectorString()).append(" ");
			}
			first = false;
			if (useBraces) {
				sb.append("(");
			}
			sb.append(formula);
			if (useBraces) {
				sb.append(")");
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the trimming formula if one of the trimmed sub-formulas is equal
	 * to it.
	 */
	@Override
	public Formula trim() {
		final Set<Formula> formulas = getFormulas();
		final ArrayList<Formula> trimmedFormulas = new ArrayList<Formula>(
				formulas.size());
		final Formula trimmingFormula = getTrimmingConstant();
		final Formula dummyFormula = getDummyConstant();
		final Class<? extends AssociativeConnectorFormula> clazz = getClass();
		for (final Formula formula : formulas) {
			final Formula trimmed = formula.trim();
			if (trimmed.equals(trimmingFormula)) {
				return trimmingFormula;
			}
			if (!trimmed.equals(dummyFormula)) {
				if (trimmed instanceof Literal) {
					if (trimmedFormulas.contains(((Literal) trimmed).opposite())) {
						return trimmingFormula;
					}
				}
				if (clazz.equals(trimmed.getClass())) {
					// Incorporate formulas if it uses the same type of connector
					trimmedFormulas.addAll(((AssociativeConnectorFormula) trimmed).getFormulas());
				} else {
					trimmedFormulas.add(trimmed);
				}
			}
		}
		if (trimmedFormulas.size() == 1) {
			return trimmedFormulas.get(0);
		} else {
			try {
				return getClass().getConstructor(Collection.class).newInstance(trimmedFormulas);
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((formulas == null) ? 0 : formulas.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AssociativeConnectorFormula other = (AssociativeConnectorFormula) obj;
		if (formulas == null) {
			if (other.formulas != null) {
				return false;
			}
		} else if (!formulas.equals(other.formulas)) {
			return false;
		}
		return true;
	}

	public abstract Formula getDummyConstant();

	public abstract Formula getTrimmingConstant();

	public abstract CharSequence getConnectorString();

}
