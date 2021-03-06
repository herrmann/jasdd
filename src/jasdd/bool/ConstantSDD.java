package jasdd.bool;

import jasdd.JASDD;
import jasdd.logic.Constant;
import jasdd.logic.Formula;
import jasdd.logic.Variable;
import jasdd.visitor.SDDVisitor;
import jasdd.vtree.InternalVTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


/**
 * Constant SDD, which is either true or false.
 *
 * @author Ricardo Herrmann
 */
public class ConstantSDD extends TerminalSDD {

	private final boolean sign;

	/* package */ ConstantSDD(final boolean sign) {
		this.sign = sign;
	}

	private ConstantSDD(final ConstantSDD sdd) {
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
		expansion.add(JASDD.createElement(true, getSign()));
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ConstantSDD other = (ConstantSDD) obj;
		if (sign != other.sign) {
			return false;
		}
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

	@Override
	public Formula getFormula() {
		return new Constant(sign);
	}

	@Override
	public boolean eval(final Set<Variable> trueLiterals) {
		return sign;
	}

	@Override
	public ConstantSDD not() {
		return JASDD.createConstant(!getSign());
	}

	@Override
	public DecompositionSDD nest(final InternalVTree vtree) {
		return JASDD.createDecomposition(vtree, JASDD.createElement(true, this));
	}

	@Override
	public int compareTo(final SDD other) {
		return -other.compareToConstant(this);
	}

	@Override
	public int compareToConstant(final ConstantSDD other) {
		return new Constant(getSign()).compareTo(new Constant(other.getSign()));
	}

	@Override
	public int compareToLiteral(final LiteralSDD other) {
		return 1;
	}

	@Override
	public int compareToDecomposition(final DecompositionSDD other) {
		return 1;
	}

}
