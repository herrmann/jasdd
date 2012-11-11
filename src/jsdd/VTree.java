package jsdd;

import java.util.Set;

import util.StringBuildable;

/**
 * A binary tree for partitioning variables.
 *  
 * @author Ricardo Herrmann
 */
public abstract class VTree implements StringBuildable {

	public abstract Set<Variable> variables();

	public abstract boolean isLeaf();

	public abstract boolean isRightLinear();

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

}
