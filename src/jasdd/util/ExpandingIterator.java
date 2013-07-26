package jasdd.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Cloneable queue-based iterator exposing the addition of elements during
 * traversal.
 *
 * @author Ricardo Herrmann
 *
 * @param <E> type of elements
 */
public class ExpandingIterator<E> implements CloneableIterator<E> {

	private final Queue<E> queue = new LinkedList<E>();

	public ExpandingIterator(final Collection<? extends E> elements) {
		queue.addAll(elements);
	}

	public ExpandingIterator(final E... elements) {
		this(Arrays.asList(elements));
	}

	public static <E> ExpandingIterator<E> build(final E... elements) {
		return new ExpandingIterator<E>(elements);
	}

	@Override
	public boolean hasNext() {
		return queue.peek() != null;
	}

	@Override
	public E next() {
		final E element = queue.poll();
		if (null != element) {
			return element;
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public void add(final E... e) {
		queue.addAll(Arrays.asList(e));
	}

	@Override
	public ExpandingIterator<E> clone() {
		return new ExpandingIterator<E>(queue);
	}

}
