package jasdd.bool;

/**
 * Additional methods used for making it easier to implement Comparable for SDDs
 * using double-dispatch.
 *
 * @author Ricardo Herrmann
 */
public interface ComparableSDD extends Comparable<SDD> {

	int compareToConstant(ConstantSDD other);

	int compareToLiteral(LiteralSDD other);

	int compareToDecomposition(DecompositionSDD other);

}
