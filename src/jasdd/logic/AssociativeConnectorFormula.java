package jasdd.logic;

import java.util.Collections;
import java.util.List;

/**
 * Template for formulas composed of a list of sub-formulas joined by a specific
 * associative boolean connector.
 *
 * @author Ricardo Herrmann
 */
public abstract class AssociativeConnectorFormula implements Formula {

	private final List<Formula> formulas;

	public AssociativeConnectorFormula(final List<Formula> formulas) {
		this.formulas = Collections.unmodifiableList(formulas);
	}

	public List<Formula> getFormulas() {
		return formulas;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (final Formula formula : formulas) {
			final boolean terminal = formula instanceof TerminalFormula;
			if (!first) {
				sb.append(" ").append(getConnectorString()).append(" ");
			}
			first = false;
			if (!terminal) {
				sb.append("(");
			}
			sb.append(formula);
			if (!terminal) {
				sb.append(")");
			}
		}
		return sb.toString();
	}

	public abstract CharSequence getConnectorString();

}
