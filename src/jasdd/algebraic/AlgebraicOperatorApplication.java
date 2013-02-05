package jasdd.algebraic;

/**
 * Algebraic operator application with node sharing and cached computation.
 * 
 * @author Ricardo Herrmann
 */
public class AlgebraicOperatorApplication {

	public static <T extends Number> ASDD<T> sum(final ASDD<T> left, final ASDD<T> right) {
		return NumericSumOperation.evaluate(left, right);
	}

	public static <T extends Number> ASDD<T> max(final ASDD<T> left, final ASDD<T> right) {
		return NumericMaxOperation.evaluate(left, right);
	}

}
