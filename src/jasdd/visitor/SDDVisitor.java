package jasdd.visitor;

import jasdd.bool.ConstantSDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.bool.LiteralSDD;

/**
 * Interface for SDD visitors.
 * 
 * @author Ricardo Herrmann
 */
public interface SDDVisitor {

	/**
	 * Called when visiting a decomposition, before sub-elements.
	 * 
	 * @param sdd the SDD to visit
	 * @return whether to visit the sub-structures
	 */
	boolean visit(DecompositionSDD sdd);

	void postVisit(DecompositionSDD sdd);

	/**
	 * Called when visiting an element, before prime and sub.
	 * 
	 * @param elem the element to visit
	 * @return whether to visit the sub-structures
	 */
	boolean visit(Element elem);

	void postVisit(Element elem);

	void visit(LiteralSDD sdd);

	void visit(ConstantSDD sdd);

}
