package jasdd.algebraic;

import jasdd.bool.SDD;
import jasdd.logic.Variable;
import jasdd.vtree.InternalAVTree;

import java.util.List;

public interface ASDDFactory {

	<T> DecompositionASDD<T> createDecomposition(InternalAVTree avtree,
			AlgebraicElement<T>... elements);

	<T> AlgebraicTerminal<T> createTerminal(T value);

	<T> AlgebraicElement<T> createElement(SDD prime, ASDD<T> sub);

	<T> AlgebraicElement<T>[] shannon(Variable v, ASDD<T> high, ASDD<T> low);

	<T> DecompositionASDD<T> createDecomposition(InternalAVTree avtree,
			List<AlgebraicElement<T>> elements);

	<T> AlgebraicElement<T> createElement(ASDD<T> sub);

	<T> AlgebraicElement<T> createElement(Variable v1, boolean s1, ASDD<T> sub);

	<T> AlgebraicElement<T> createElement(Variable v1, ASDD<T> sub);

}