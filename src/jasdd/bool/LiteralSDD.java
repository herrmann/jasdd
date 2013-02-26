package jasdd.bool;

import jasdd.JASDD;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.visitor.SDDVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


/**
 * SDD for a single literal.
 *
 * @author Ricardo Herrmann
 */
public class LiteralSDD extends TerminalSDD {

	private final Literal literal;

	private LiteralSDD(final LiteralSDD sdd) {
		this.literal = new Literal(sdd.getLiteral());
	}

	private LiteralSDD(final int index, final boolean sign) {
		this.literal = new Literal(new Variable(index), sign);
	}

	private LiteralSDD(final int index) {
		this.literal = new Literal(new Variable(index), true);
	}

	/* package */ LiteralSDD(final Variable variable, final boolean sign) {
		this.literal = new Literal(variable, sign);
	}

	private LiteralSDD(final Variable variable) {
		this(variable, true);
	}

	public Literal getLiteral() {
		return literal;
	}

	@Override
	public StringBuilder toStringBuilder() {
		return new StringBuilder(literal.toString());
	}

	@Override
	public boolean isTautology() {
		return false;
	}

	@Override
	public boolean isUnsatisfiable() {
		return false;
	}

	@Override
	public boolean isFalse() {
		return false;
	}

	@Override
	public Collection<Element> expansion() {
		final Collection<Element> expansion = new ArrayList<Element>(2);
		expansion.add(JASDD.createElement(getLiteral(), true));
		expansion.add(JASDD.createElement(getLiteral().opposite(), false));
		return expansion;
	}

	@Override
	public boolean isConsistent() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((literal == null) ? 0 : literal.hashCode());
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
		final LiteralSDD other = (LiteralSDD) obj;
		if (literal == null) {
			if (other.literal != null) {
				return false;
			}
		} else if (!literal.equals(other.literal)) {
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
		return new LiteralSDD(this);
	}

	@Override
	public boolean getSign() {
		return getLiteral().getSign();
	}

	@Override
	public void accept(final SDDVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Formula getFormula() {
		return new Literal(literal);
	}

	@Override
	public boolean eval(final Set<Variable> trueLiterals) {
		final Variable var = getLiteral().getVariable();
		if (trueLiterals.contains(var)) {
			return getSign();
		} else {
			return !getSign();
		}
	}

}
