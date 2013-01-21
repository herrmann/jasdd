package jasdd.algebraic;

import jasdd.bool.SDD;
import jasdd.visitor.AbstractASDDVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ricardo Herrmann
 */
public class NumericSumOperation<T extends Number> extends AbstractASDDVisitor<T> {

	final ASDD<T> left, right;
	public ASDD<T> result;

	public NumericSumOperation(final ASDD<T> left, final ASDD<T> right) {
		this.left = left;
		this.right = right;
	}

	public ASDD<T> getResult() {
		if (null == result) {
			left.accept(this);
		}
		return result;
	}

	@Override
	public void visit(final AlgebraicTerminal<T> terminal) {
		right.accept(new AbstractASDDVisitor<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public void visit(final AlgebraicTerminal<T> other) {
				final Double sum = terminal.getValue().doubleValue() + other.getValue().doubleValue();
				result = (ASDD<T>) new AlgebraicTerminal<Double>(sum);
			}

			@Override
			public boolean visit(final DecompositionASDD<T> decomp) {
				result = addScalarToDecomposition(decomp, terminal);
				return false;
			}

		});
	}

	@Override
	public boolean visit(final DecompositionASDD<T> decomp) {
		right.accept(new AbstractASDDVisitor<T>() {

			@Override
			public void visit(final AlgebraicTerminal<T> terminal) {
				result = addScalarToDecomposition(decomp, terminal);
			}

			@Override
			public boolean visit(final DecompositionASDD<T> other) {
				throw new UnsupportedOperationException();
			}

		});
		return false;
	}

	private ASDD<T> addScalarToDecomposition(final DecompositionASDD<T> decomp, final AlgebraicTerminal<T> terminal) {
		// TODO: structural caching
		final List<AlgebraicElement<T>> origElements = decomp.getElements();
		final List<AlgebraicElement<T>> elements = new ArrayList<AlgebraicElement<T>>(origElements.size());
		for (final AlgebraicElement<T> element : origElements) {
			final SDD prime = element.getPrime();
			final ASDD<T> sub = AlgebraicOperatorApplication.sum(element.getSub(), terminal);
			elements.add(new AlgebraicElement<T>(prime, sub));
		}
		return new DecompositionASDD<T>(decomp.getTree(), elements);
	}

	public static <T extends Number> ASDD<T> evaluate(final ASDD<T> left, final ASDD<T> right) {
		final NumericSumOperation<T> visitor = new NumericSumOperation<T>(left, right);
		return visitor.getResult();
	}

}
