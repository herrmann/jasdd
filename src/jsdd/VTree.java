package jsdd;

import java.util.Set;

import util.StringBuildable;

public abstract class VTree implements StringBuildable {

	public abstract Set<Variable> variables();

	public abstract boolean isLeaf();

	public abstract boolean isRightLinear();

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

}
