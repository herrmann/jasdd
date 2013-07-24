package jasdd.util;

import java.util.Iterator;

/**
 * Cloneable iterator to avoid having to pass the original container around.
 *
 * @author Ricardo Herrmann
 *
 * @param <E> type of elements
 */
public interface CloneableIterator<E> extends Iterator<E>, Cloneable {

	CloneableIterator<E> clone();

}
