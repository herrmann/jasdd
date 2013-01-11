package jasdd.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Base class for lazily generating hierarchical set partitions. Implementations
 * must provide a combiner for joining the left and right sides of partitions.
 * 
 * @author Ricardo Herrmann
 */
public class PartitioningIterator<T> implements Iterator<T>, Iterable<T> {

	private final T[] elements;
	private final int begin, end;

	private int sep;

	private Iterator<T> left, right;

	private T leftPartition;

	public interface Combiner<T> {
		T combine(T leftPartition, T rightPartition, final T[] elements, int begin, int end);
	}

	private Combiner<T> combiner;

	public PartitioningIterator(final T[] elements, final Combiner<T> combiner) {
		this(elements, 0, elements.length, combiner);
	}

	private PartitioningIterator(final T[] elements, final int begin, final int end, final Combiner<T> combiner) {
		this.elements = elements;
		this.begin = begin;
		this.end = end;
		this.combiner = combiner;
		sep = begin + 1;
		left = buildIterator(begin, sep);
		right = buildIterator(sep, end);
		leftPartition = left.next();
	}

	private Iterator<T> buildIterator(final int begin, final int end) {
		if (end - begin == 1) {
			return new SingleElementIterator<T>(elements[begin]);
		} else {
			return new PartitioningIterator<T>(elements, begin, end, combiner);
		}
	}

	@Override
	public boolean hasNext() {
		return right.hasNext() || left.hasNext() || sep < end - 1;
	}

	@Override
	public T next() {
		if (right.hasNext()) {
			final T rightPartition = right.next();
			return combiner.combine(leftPartition, rightPartition, elements, begin, end);
		} else if (left.hasNext()) {
			leftPartition = left.next();
			right = buildIterator(sep, end);
			return next();
		} else if (sep < end - 1) {
			sep++;
			left = buildIterator(begin, sep);
			return next();
		} else {
			throw new NoSuchElementException();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

}
