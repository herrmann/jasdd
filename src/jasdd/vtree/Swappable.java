package jasdd.vtree;

import jasdd.util.CloneableIterator;

import java.util.Iterator;

/**
 * Interface for potentially swappable binary structures.
 *
 * @author Ricardo Herrmann
 */
public interface Swappable<T> {

	boolean canSwap();

	boolean canSwap(Direction... path);

	boolean canSwap(Iterator<Direction> path);

	T swap();

	T swap(Direction... path);

	T swap(CloneableIterator<Direction> path);

}
