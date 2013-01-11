package jasdd.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for a single element.
 *  
 * @author Ricardo Herrmann
 */
public class SingleElementIterator<T> implements Iterator<T> {

	private T element;

	public SingleElementIterator(final T element) {
		this.element = element;
	}

	@Override
	public boolean hasNext() {
		return element != null;
	}

	@Override
	public T next() {
		if (element == null) {
			throw new NoSuchElementException();
		} else {
			final T next = element;
			element = null;
			return next;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
