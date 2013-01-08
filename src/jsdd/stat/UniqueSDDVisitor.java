package jsdd.stat;

import java.util.HashSet;
import java.util.Set;

import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.Element;
import jsdd.LiteralSDD;

/**
 * Visits each component of an SDD only once, delegating the visiting logic to
 * the wrapped visitor.
 * 
 * @author Ricardo Herrmann
 */
public class UniqueSDDVisitor implements SDDVisitor {

	private final SDDVisitor delegate;

	private final Set<DecompositionSDD> visitedDecompositions = new HashSet<DecompositionSDD>();

	private final Set<Element> visitedElements = new HashSet<Element>();

	public UniqueSDDVisitor(final SDDVisitor delegate) {
		this.delegate = delegate;
	}

	public SDDVisitor getDelegate() {
		return delegate;
	}

	@Override
	public boolean visit(final DecompositionSDD sdd) {
		if (!visited(sdd)) {
			markVisited(sdd);
			getDelegate().visit(sdd);
			return true;
		}
		return false;
	}

	@Override
	public void postVisit(final DecompositionSDD sdd) {
		getDelegate().postVisit(sdd);
	}

	@Override
	public boolean visit(final Element elem) {
		if (!visited(elem)) {
			markVisited(elem);
			getDelegate().visit(elem);
			return true;
		}
		return false;
	}

	@Override
	public void postVisit(final Element elem) {
		getDelegate().postVisit(elem);
	}

	@Override
	public void visit(final LiteralSDD sdd) {
		getDelegate().visit(sdd);
	}

	@Override
	public void visit(final ConstantSDD sdd) {
		getDelegate().visit(sdd);
	}

	private boolean visited(final DecompositionSDD sdd) {
		return visitedDecompositions.contains(sdd);
	}

	private void markVisited(final DecompositionSDD sdd) {
		visitedDecompositions.add(sdd);
	}

	private boolean visited(final Element elem) {
		return visitedElements.contains(elem);
	}

	private void markVisited(final Element elem) {
		visitedElements.add(elem);
	}

}
