package jasdd.util;

import java.util.NoSuchElementException;

/**
 * Cloneable array iterator to avoid having to pass the original array around.
 *
 * @author Ricardo Herrmann
 *
 * @param <E> type of array elements
 */
public class CloneableArrayIterator<E> implements CloneableIterator<E> {

	private final E[] array;
	private int next = 0;

	public CloneableArrayIterator(final E... array) {
		this.array = array;
	}

	public static <E> CloneableArrayIterator<E> build(final E... array) {
		return new CloneableArrayIterator<E>(array);
	}

	@Override
	public boolean hasNext() {
		return next < array.length;
	}

	@Override
	public E next() {
		if (hasNext()) {
			return array[next++];
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public CloneableArrayIterator<E> clone() {
		try {
			return (CloneableArrayIterator<E>) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
