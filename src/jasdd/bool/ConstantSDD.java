package jasdd.bool;

import jasdd.visitor.SDDVisitor;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Constant SDD, which is either true or false.
 * 
 * @author Ricardo Herrmann
 */
public class ConstantSDD extends TerminalSDD {

	private boolean sign;

	public ConstantSDD(final boolean sign) {
		this.sign = sign;
	}

	public ConstantSDD(final ConstantSDD sdd) {
		this(sdd.getSign());
	}

	@Override
	public boolean getSign() {
		return sign;
	}

	@Override
	public boolean isTautology() {
		return sign;
	}

	@Override
	public boolean isUnsatisfiable() {
		return !sign;
	}

	@Override
	public boolean isFalse() {
		return isUnsatisfiable();
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(sign ? TRUE : FALSE);
	}

	@Override
	public Collection<Element> expansion() {
		final Collection<Element> expansion = new ArrayList<Element>(1);
		expansion.add(new Element(true, getSign()));
		return expansion;
	}

	@Override
	public boolean isConsistent() {
		return getSign();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (sign ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConstantSDD other = (ConstantSDD) obj;
		if (sign != other.sign)
			return false;
		return true;
	}

	@Override
	public void dump() {
		// TODO Auto-generated method stub
	}

	@Override
	public SDD trimmed() {
		return new ConstantSDD(this);
	}

	@Override
	public void accept(final SDDVisitor visitor) {
		visitor.visit(this);
	}

}