package jsdd;

import jsdd.vtree.VTree;

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

	@Override
	public boolean isTerminal() {
		return true;
	}

	public abstract boolean getSign();

}
