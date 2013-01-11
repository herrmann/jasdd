package jasdd.visitor;

import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.DecompositionASDD;

/**
 * Interface for ASDD visitors.
 * 
 * @author Ricardo Herrmann
 */
public interface ASDDVisitor<T> extends SDDVisitor {

	/**
	 * Called when visiting an algebraic decomposition, before sub-elements.
	 * 
	 * @param asdd the SDD to visit
	 * @return whether to visit the sub-structures
	 */
	boolean visit(DecompositionASDD<T> asdd);

	void postVisit(DecompositionASDD<T> asdd);

	/**
	 * Called when visiting an algebraic element, before prime and sub.
	 * 
	 * @param elem the element to visit
	 * @return whether to visit the sub-structures
	 */
	boolean visit(AlgebraicElement<T> elem);

	void postVisit(AlgebraicElement<T> elem);

	void visit(AlgebraicTerminal<T> asdd);

}
