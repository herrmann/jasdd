package jsdd;

/**
 * Superclass for all kinds of terminal SDDs (constant and literal).
 * 
 * @author Ricardo Herrmann
 */
public abstract class TerminalSDD extends AbstractSDD {

	@Override
	public VTree getVTree() {
		return getParent().getVTree();
	}

}
