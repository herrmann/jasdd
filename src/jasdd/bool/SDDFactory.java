package jasdd.bool;

import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.vtree.InternalVTree;

public interface SDDFactory {

	ConstantSDD createConstant(boolean sign);

	ConstantSDD createFalse();

	ConstantSDD createTrue();

	LiteralSDD createLiteral(Variable variable, boolean sign);

	DecompositionSDD createDecomposition(InternalVTree node,
			Element... elements);

	/**
	 * Base factory method for Elements.
	 *
	 * @param prime the prime SDD
	 * @param sub the sub SDD
	 * @return the cached instance of the Element
	 */
	Element createElement(SDD prime, SDD sub);

	SDD createLiteral(LiteralSDD sdd);

	SDD createDecomposition(DecompositionSDD sdd);

	Element createElement(Variable v1, boolean s1, Variable v2, boolean s2);

	Element createElement(Variable v1, Variable v2);

	Element createElement(Variable v1, boolean s2);

	Element createElement(boolean s1, Variable v2);

	Element createElement(boolean s1, Literal l2);

	Element createElement(Variable v1, boolean s1, Variable v2);

	Element createElement(Variable v1, Variable v2, boolean s2);

	Element createElement(Variable v1, boolean s1, boolean s2);

	Element createElement(boolean s1, Variable v2, boolean s2);

	Element createElement(boolean s1, boolean s2);

	Element createElement(SDD prime, boolean s2);

	Element createElement(boolean s1, SDD sub);

	Element createElement(SDD prime, Variable v2, boolean s2);

	Element createElement(SDD prime, Variable v2);

	Element createElement(Variable v1, boolean s1, SDD sub);

	Element createElement(Variable v1, SDD sub);

	Element createElement(Literal l1, boolean s2);

	Element createElement(Literal l1, Literal l2);

	Element[] shannon(Variable v, Variable v1, Variable v2, boolean s2);

	Element[] shannon(Variable v, Variable v1, boolean s1, Variable v2);

	Element[] shannon(Variable v, Variable v1, Variable v2);

	Element[] shannon(Variable v, Variable v1, boolean s1, Variable v2,
			boolean s2);

	Element[] shannon(Variable v, Variable v1, boolean s2);

	Element[] shannon(Variable v, Variable v1, boolean s1, boolean s2);

	Element[] shannon(Variable v, boolean s1, Variable v2);

	Element[] shannon(Variable v, boolean s1, Variable v2, boolean s2);

	Element[] shannon(Variable v, boolean s1, boolean s2);

	LiteralSDD createLiteral(int index, boolean sign);

	LiteralSDD createLiteral(int index);

	LiteralSDD createLiteral(Variable variable);

	LiteralSDD createLiteral(Literal lit);

}