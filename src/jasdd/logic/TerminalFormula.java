package jasdd.logic;

/**
 * Type to denote a formula which is a terminal node in an expression's tree.
 *
 * @author Ricardo Herrmann
 */
public abstract class TerminalFormula implements Formula {

	@Override
	public Formula trim() {
		return this;
	}

}
