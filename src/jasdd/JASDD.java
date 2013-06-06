package jasdd;

import jasdd.algebraic.ASDD;
import jasdd.algebraic.ASDDFactory;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.CachingASDDFactory;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.CachingSDDFactory;
import jasdd.bool.ConstantSDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.bool.LiteralSDD;
import jasdd.bool.SDD;
import jasdd.bool.SDDFactory;
import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;

import java.util.List;

/**
 * Façade for the commonly used facilities provided by JASDD.
 *
 * @author Ricardo Herrmann
 */
public class JASDD {

	private static SDDFactory getFactory() {
		return CachingSDDFactory.getInstance();
	}

	private static ASDDFactory getAlgFactory() {
		return CachingASDDFactory.getInstance();
	}

	public static ConstantSDD createConstant(final boolean sign) {
		return getFactory().createConstant(sign);
	}

	public static ConstantSDD createFalse() {
		return getFactory().createFalse();
	}

	public static ConstantSDD createTrue() {
		return getFactory().createTrue();
	}

	public static LiteralSDD createLiteral(final Variable variable, final boolean sign) {
		return getFactory().createLiteral(variable, sign);
	}

	public static DecompositionSDD createDecomposition(final InternalVTree node, final Element... elements) {
		return getFactory().createDecomposition(node, elements);
	}

	public static Element createElement(final SDD prime, final SDD sub) {
		return getFactory().createElement(prime, sub);
	}

	public static SDD createLiteral(final LiteralSDD sdd) {
		return getFactory().createLiteral(sdd);
	}

	public static SDD createDecomposition(final DecompositionSDD sdd) {
		return getFactory().createDecomposition(sdd);
	}

	public static Element createElement(final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().createElement(v1, s1, v2, s2);
	}

	public static Element createElement(final Variable v1, final Variable v2) {
		return getFactory().createElement(v1, v2);
	}

	public static Element createElement(final Variable v1, final boolean s2) {
		return getFactory().createElement(v1, s2);
	}

	public static Element createElement(final boolean s1, final Variable v2) {
		return getFactory().createElement(s1, v2);
	}

	public static Element createElement(final boolean s1, final Literal l2) {
		return getFactory().createElement(s1, l2);
	}

	public static Element createElement(final Variable v1, final boolean s1, final Variable v2) {
		return getFactory().createElement(v1, s1, v2);
	}

	public static Element createElement(final Variable v1, final Variable v2, final boolean s2) {
		return getFactory().createElement(v1, v2, s2);
	}

	public static Element createElement(final Variable v1, final boolean s1, final boolean s2) {
		return getFactory().createElement(v1, s1, s2);
	}

	public static Element createElement(final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().createElement(s1, v2, s2);
	}

	public static Element createElement(final boolean s1, final boolean s2) {
		return getFactory().createElement(s1, s2);
	}

	public static Element createElement(final SDD prime, final boolean s2) {
		return getFactory().createElement(prime, s2);
	}

	public static Element createElement(final boolean s1, final SDD sub) {
		return getFactory().createElement(s1, sub);
	}

	public static Element createElement(final SDD prime, final Variable v2, final boolean s2) {
		return getFactory().createElement(prime, v2, s2);
	}

	public static Element createElement(final SDD prime, final Variable v2) {
		return getFactory().createElement(prime, v2);
	}

	public static Element createElement(final Variable v1, final boolean s1, final SDD sub) {
		return getFactory().createElement(v1, s1, sub);
	}

	public static Element createElement(final Variable v1, final SDD sub) {
		return getFactory().createElement(v1, sub);
	}

	public static Element createElement(final Literal l1, final boolean s2) {
		return getFactory().createElement(l1, s2);
	}

	public static Element createElement(final Literal l1, final Literal l2) {
		return getFactory().createElement(l1, l2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final Variable v2, final boolean s2) {
		return getFactory().shannon(v, v1, v2, s2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2) {
		return getFactory().shannon(v, v1, s1, v2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final Variable v2) {
		return getFactory().shannon(v, v1, v2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().shannon(v, v1, s1, v2, s2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s2) {
		return getFactory().shannon(v, v1, s2);
	}

	public static Element[] shannon(final Variable v, final Variable v1, final boolean s1, final boolean s2) {
		return getFactory().shannon(v, v1, s1, s2);
	}

	public static Element[] shannon(final Variable v, final boolean s1, final Variable v2) {
		return getFactory().shannon(v, s1, v2);
	}

	public static Element[] shannon(final Variable v, final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().shannon(v, s1, v2, s2);
	}

	public static Element[] shannon(final Variable v, final boolean s1, final boolean s2) {
		return getFactory().shannon(v, s1, s2);
	}

	public static LiteralSDD createLiteral(final int index, final boolean sign) {
		return getFactory().createLiteral(index, sign);
	}

	public static LiteralSDD createLiteral(final int index) {
		return getFactory().createLiteral(index);
	}

	public static LiteralSDD createLiteral(final Variable variable) {
		return getFactory().createLiteral(variable);
	}

	public static <T> DecompositionASDD<T> createDecomposition(final InternalAVTree avtree, final AlgebraicElement<T>... elements) {
		return getAlgFactory().createDecomposition(avtree, elements);
	}

	public static <T> AlgebraicTerminal<T> createTerminal(final T value) {
		return getAlgFactory().createTerminal(value);
	}

	public static <T> AlgebraicElement<T> createElement(final SDD prime, final ASDD<T> sub) {
		return getAlgFactory().createElement(prime, sub);
	}

	public static <T> AlgebraicElement<T>[] shannon(final Variable v, final ASDD<T> high, final ASDD<T> low) {
		return getAlgFactory().shannon(v, high, low);
	}

	public static <T> DecompositionASDD<T> createDecomposition(final InternalAVTree avtree, final List<AlgebraicElement<T>> elements) {
		return getAlgFactory().createDecomposition(avtree, elements);
	}

	public static <T> AlgebraicElement<T> createElement(final ASDD<T> sub) {
		return getAlgFactory().createElement(sub);
	}

	public static <T> AlgebraicElement<T> createElement(final Variable v1, final boolean s1, final ASDD<T> sub) {
		return getAlgFactory().createElement(v1, s1, sub);
	}

	public static <T> AlgebraicElement<T> createElement(final Variable v1, final ASDD<T> sub) {
		return getAlgFactory().createElement(v1, sub);
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Variable v, final boolean sign) {
		return DecompositionSDD.buildNormalized(vtree, v, sign);
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Variable v) {
		return DecompositionSDD.buildNormalized(vtree, v);
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final int index) {
		return DecompositionSDD.buildNormalized(vtree, index);
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Literal lit) {
		return DecompositionSDD.buildNormalized(vtree, lit);
	}

}
