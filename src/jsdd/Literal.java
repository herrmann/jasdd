package jsdd;

public class Literal {

	private Variable variable;
	private boolean sign;
	
	public Literal(final Variable variable, final boolean sign) {
		this.variable = variable;
		this.sign = sign;
	}

	@Override
	public String toString() {
		return (sign ? "" : "-") + variable.toString();
	}
	
}
