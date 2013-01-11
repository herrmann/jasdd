package jasdd.visitor;

import jasdd.bool.ConstantSDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.bool.LiteralSDD;

/**
 * Empty implementation of SDDVisitor meant to be used as base class for
 * simplifying concrete implementations.
 * 
 * @author Ricardo Herrmann
 */
public abstract class AbstractSDDVisitor implements SDDVisitor {

	@Override
	public boolean visit(final DecompositionSDD sdd) {
		return true;
	}

	@Override
	public void postVisit(final DecompositionSDD sdd) {
	}

	@Override
	public boolean visit(final Element elem) {
		return true;
	}

	@Override
	public void postVisit(final Element elem) {
	}

	@Override
	public void visit(final LiteralSDD sdd) {
	}

	@Override
	public void visit(final ConstantSDD sdd) {
	}

}
