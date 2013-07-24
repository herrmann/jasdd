package jasdd.vtree;

import java.util.Iterator;

/**
 * Interface for potentially rotatable binary structures.
 *
 * @author Ricardo Herrmann
 */
public interface Rotatable<T> {

	enum Direction {
		LEFT, RIGHT
	};

	boolean canRotateLeft();

	boolean canRotateRight();

	boolean canRotateLeft(Direction... path);

	boolean canRotateRight(Direction... path);

	boolean canRotateLeft(Iterator<Direction> path);

	boolean canRotateRight(Iterator<Direction> path);

	T rotateLeft();

	T rotateRight();

	T rotateLeft(Direction... path);

	T rotateRight(Direction... path);

	T rotateLeft(Iterator<Direction> path);

	T rotateRight(Iterator<Direction> path);

}
