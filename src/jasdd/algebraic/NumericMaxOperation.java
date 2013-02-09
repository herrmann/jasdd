package jasdd.algebraic;

import jasdd.bool.AndOperator;
import jasdd.bool.OperatorApplication;
import jasdd.bool.SDD;
import jasdd.visitor.AbstractASDDVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ricardo Herrmann
 */
public class NumericMaxOperation<T extends Number> extends AbstractASDDVisitor<T> {

	final ASDD<T> left, right;
	public ASDD<T> result;

	public NumericMaxOperation(final ASDD<T> left, final ASDD<T> right) {
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
				final double max = Math.max(terminal.getValue().doubleValue(), other.getValue().doubleValue());
				result = (ASDD<T>) ASDDFactory.getInstance().createTerminal(max);
			}

			@Override
			public boolean visit(final DecompositionASDD<T> decomp) {
				result = maxScalarToDecomposition(decomp, terminal);
				return false;
			}

		});
	}

	@Override
	public boolean visit(final DecompositionASDD<T> decomp) {
		right.accept(new AbstractASDDVisitor<T>() {

			@Override
			public void visit(final AlgebraicTerminal<T> terminal) {
				result = maxScalarToDecomposition(decomp, terminal);
			}

			@Override
			public boolean visit(final DecompositionASDD<T> other) {
				result = maxDecompositions(decomp, other);
				return false;
			}

		});
		return false;
	}

	private ASDD<T> maxScalarToDecomposition(final DecompositionASDD<T> decomp, final AlgebraicTerminal<T> terminal) {
		// TODO: structural caching
		final List<AlgebraicElement<T>> origElements = decomp.getElements();
		final List<AlgebraicElement<T>> elements = new ArrayList<AlgebraicElement<T>>(origElements.size());
		for (final AlgebraicElement<T> element : origElements) {
			final SDD prime = element.getPrime();
			final ASDD<T> sub = AlgebraicOperatorApplication.max(element.getSub(), terminal);
			elements.add(ASDDFactory.getInstance().createElement(prime, sub));
		}
		return ASDDFactory.getInstance().createDecomposition(decomp.getTree(), elements);
	}

	private ASDD<T> maxDecompositions(final DecompositionASDD<T> decomp, final DecompositionASDD<T> other) {
		if (!decomp.getTree().equals(other.getTree())) {
			throw new UnsupportedOperationException("Maximization of decompositions using different avtrees is not supported yet");
		}
		// TODO: structural caching
		final List<AlgebraicElement<T>> elements = new ArrayList<AlgebraicElement<T>>();
		for (final AlgebraicElement<T> leftElem : decomp.getElements()) {
			for (final AlgebraicElement<T> rightElem : other.getElements()) {
				final SDD prime = new OperatorApplication(leftElem.getPrime(), rightElem.getPrime(), new AndOperator()).apply();
				final ASDD<T> sub = AlgebraicOperatorApplication.max(leftElem.getSub(), rightElem.getSub());
				// TODO: compression
				elements.add(ASDDFactory.getInstance().createElement(prime, sub));
			}
		}
		return ASDDFactory.getInstance().createDecomposition(decomp.getTree(), elements);
	}

	public static <T extends Number> ASDD<T> evaluate(final ASDD<T> left, final ASDD<T> right) {
		final NumericMaxOperation<T> visitor = new NumericMaxOperation<T>(left, right);
		return visitor.getResult();
	}

}
