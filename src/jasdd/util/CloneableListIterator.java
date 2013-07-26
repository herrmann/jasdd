package jasdd.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Cloneable list iterator to avoid having to pass the original list around.
 *
 * @author Ricardo Herrmann
 *
 * @param <E> type of list elements
 */
public class CloneableListIterator<E> implements CloneableIterator<E> {

	private final List<E> list;
	private int next = 0;

	public CloneableListIterator(final List<E> list) {
		this(list, 0);
	}

	public CloneableListIterator(final List<E> list, final int next) {
		this.list = list;
		this.next = next;
	}

	public CloneableListIterator(final E... array) {
		this.list = Arrays.asList(array);
	}

	public static <E> CloneableListIterator<E> build(final E... list) {
		return new CloneableListIterator<E>(list);
	}

	@Override
	public boolean hasNext() {
		return next < list.size();
	}

	@Override
	public E next() {
		if (hasNext()) {
			return list.get(next++);
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public CloneableListIterator<E> clone() {
		return new CloneableListIterator<E>(new ArrayList<E>(list), next);
	}

}
