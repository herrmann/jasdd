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
public class JASDD implements SDDFactory, ASDDFactory {

	private SDDFactory getFactory() {
		return CachingSDDFactory.getInstance();
	}

	private ASDDFactory getAlgFactory() {
		return CachingASDDFactory.getInstance();
	}

	@Override
	public ConstantSDD createConstant(final boolean sign) {
		return getFactory().createConstant(sign);
	}

	@Override
	public ConstantSDD createFalse() {
		return getFactory().createFalse();
	}

	@Override
	public ConstantSDD createTrue() {
		return getFactory().createTrue();
	}

	@Override
	public LiteralSDD createLiteral(final Variable variable, final boolean sign) {
		return getFactory().createLiteral(variable, sign);
	}

	@Override
	public DecompositionSDD createDecomposition(final InternalVTree node, final Element... elements) {
		return getFactory().createDecomposition(node, elements);
	}

	@Override
	public Element createElement(final SDD prime, final SDD sub) {
		return getFactory().createElement(prime, sub);
	}

	@Override
	public SDD createLiteral(final LiteralSDD sdd) {
		return getFactory().createLiteral(sdd);
	}

	@Override
	public SDD createDecomposition(final DecompositionSDD sdd) {
		return getFactory().createDecomposition(sdd);
	}

	@Override
	public Element createElement(final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().createElement(v1, s1, v2, s2);
	}

	@Override
	public Element createElement(final Variable v1, final Variable v2) {
		return getFactory().createElement(v1, v2);
	}

	@Override
	public Element createElement(final Variable v1, final boolean s2) {
		return getFactory().createElement(v1, s2);
	}

	@Override
	public Element createElement(final boolean s1, final Variable v2) {
		return getFactory().createElement(s1, v2);
	}

	@Override
	public Element createElement(final boolean s1, final Literal l2) {
		return getFactory().createElement(s1, l2);
	}

	@Override
	public Element createElement(final Variable v1, final boolean s1, final Variable v2) {
		return getFactory().createElement(v1, s1, v2);
	}

	@Override
	public Element createElement(final Variable v1, final Variable v2, final boolean s2) {
		return getFactory().createElement(v1, v2, s2);
	}

	@Override
	public Element createElement(final Variable v1, final boolean s1, final boolean s2) {
		return getFactory().createElement(v1, s1, s2);
	}

	@Override
	public Element createElement(final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().createElement(s1, v2, s2);
	}

	@Override
	public Element createElement(final boolean s1, final boolean s2) {
		return getFactory().createElement(s1, s2);
	}

	@Override
	public Element createElement(final SDD prime, final boolean s2) {
		return getFactory().createElement(prime, s2);
	}

	@Override
	public Element createElement(final boolean s1, final SDD sub) {
		return getFactory().createElement(s1, sub);
	}

	@Override
	public Element createElement(final SDD prime, final Variable v2, final boolean s2) {
		return getFactory().createElement(prime, v2, s2);
	}

	@Override
	public Element createElement(final SDD prime, final Variable v2) {
		return getFactory().createElement(prime, v2);
	}

	@Override
	public Element createElement(final Variable v1, final boolean s1, final SDD sub) {
		return getFactory().createElement(v1, s1, sub);
	}

	@Override
	public Element createElement(final Variable v1, final SDD sub) {
		return getFactory().createElement(v1, sub);
	}

	@Override
	public Element createElement(final Literal l1, final boolean s2) {
		return getFactory().createElement(l1, s2);
	}

	@Override
	public Element createElement(final Literal l1, final Literal l2) {
		return getFactory().createElement(l1, l2);
	}

	@Override
	public Element[] shannon(final Variable v, final Variable v1, final Variable v2, final boolean s2) {
		return getFactory().shannon(v, v1, v2, s2);
	}

	@Override
	public Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2) {
		return getFactory().shannon(v, v1, s1, v2);
	}

	@Override
	public Element[] shannon(final Variable v, final Variable v1, final Variable v2) {
		return getFactory().shannon(v, v1, v2);
	}

	@Override
	public Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().shannon(v, v1, s1, v2, s2);
	}

	@Override
	public Element[] shannon(final Variable v, final Variable v1, final boolean s2) {
		return getFactory().shannon(v, v1, s2);
	}

	@Override
	public Element[] shannon(final Variable v, final Variable v1, final boolean s1, final boolean s2) {
		return getFactory().shannon(v, v1, s1, s2);
	}

	@Override
	public Element[] shannon(final Variable v, final boolean s1, final Variable v2) {
		return getFactory().shannon(v, s1, v2);
	}

	@Override
	public Element[] shannon(final Variable v, final boolean s1, final Variable v2, final boolean s2) {
		return getFactory().shannon(v, s1, v2, s2);
	}

	@Override
	public Element[] shannon(final Variable v, final boolean s1, final boolean s2) {
		return getFactory().shannon(v, s1, s2);
	}

	@Override
	public LiteralSDD createLiteral(final int index, final boolean sign) {
		return getFactory().createLiteral(index, sign);
	}

	@Override
	public LiteralSDD createLiteral(final int index) {
		return getFactory().createLiteral(index);
	}

	@Override
	public LiteralSDD createLiteral(final Variable variable) {
		return getFactory().createLiteral(variable);
	}

	@Override
	public <T> DecompositionASDD<T> createDecomposition(final InternalAVTree avtree, final AlgebraicElement<T>... elements) {
		return getAlgFactory().createDecomposition(avtree, elements);
	}

	@Override
	public <T> AlgebraicTerminal<T> createTerminal(final T value) {
		return getAlgFactory().createTerminal(value);
	}

	@Override
	public <T> AlgebraicElement<T> createElement(final SDD prime, final ASDD<T> sub) {
		return getAlgFactory().createElement(prime, sub);
	}

	@Override
	public <T> AlgebraicElement<T>[] shannon(final Variable v, final ASDD<T> high, final ASDD<T> low) {
		return getAlgFactory().shannon(v, high, low);
	}

	@Override
	public <T> DecompositionASDD<T> createDecomposition(final InternalAVTree avtree, final List<AlgebraicElement<T>> elements) {
		return getAlgFactory().createDecomposition(avtree, elements);
	}

	@Override
	public <T> AlgebraicElement<T> createElement(final ASDD<T> sub) {
		return getAlgFactory().createElement(sub);
	}

	@Override
	public <T> AlgebraicElement<T> createElement(final Variable v1, final boolean s1, final ASDD<T> sub) {
		return getAlgFactory().createElement(v1, s1, sub);
	}

	@Override
	public <T> AlgebraicElement<T> createElement(final Variable v1, final ASDD<T> sub) {
		return getAlgFactory().createElement(v1, sub);
	}

}
