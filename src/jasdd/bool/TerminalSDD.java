package jasdd.bool;

/**
 * Superclass for all kinds of terminal SDDs (constant and literal).
 * 
 * @author Ricardo Herrmann
 */
public abstract class TerminalSDD extends AbstractSDD {

	@Override
	public boolean isTerminal() {
		return true;
	}

	public abstract boolean getSign();

	@Override
	public int size() {
		return 0;
	}

}
