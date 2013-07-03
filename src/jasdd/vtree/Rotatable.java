package jasdd.vtree;

/**
 * Interface for potentially rotatable structures.
 *
 * @author Ricardo Herrmann
 */
public interface Rotatable<T> {

	boolean canRotateLeft();

	boolean canRotateRight();

	T rotateLeft();

	T rotateRight();

}
