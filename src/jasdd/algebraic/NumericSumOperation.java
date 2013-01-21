package jasdd.algebraic;

import jasdd.visitor.AbstractASDDVisitor;

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
		return result;
	}

	@Override
	public void visit(final AlgebraicTerminal<T> asdd) {
		right.accept(new AbstractASDDVisitor<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public void visit(final AlgebraicTerminal<T> other) {
				final Double sum = asdd.getValue().doubleValue() + other.getValue().doubleValue();
				result = (ASDD<T>) new AlgebraicTerminal<Double>(sum);
			}

			@Override
			public boolean visit(final DecompositionASDD<T> asdd) {
				// TODO: implement
				throw new UnsupportedOperationException();
			}

		});
	}

	@Override
	public boolean visit(final DecompositionASDD<T> asdd) {
		// TODO: implement
		throw new UnsupportedOperationException();
	}

	public ASDD<T> dispatch() {
		left.accept(this);
		return getResult();
	}

	public static <T extends Number> ASDD<T> evaluate(final ASDD<T> left, final ASDD<T> right) {
		final NumericSumOperation<T> visitor = new NumericSumOperation<T>(left, right);
		return visitor.dispatch();
	}

}
