package jsdd;

public class Variable {

	private int index;

	public Variable(final int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return Integer.toString(getIndex());
	}

}
