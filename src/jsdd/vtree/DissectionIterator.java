package jsdd.vtree;

import java.util.Iterator;
import java.util.NoSuchElementException;

import util.SingleElementIterator;

/**
 * Lazily generates tree dissections.
 * 
 * @author Ricardo Herrmann
 */
public class DissectionIterator implements Iterator<Tree>, Iterable<Tree> {

	private final Tree[] leaves;
	private final int begin, end;

	private int sep;

	private Iterator<Tree> left, right;

	private Tree leftTree;

	public DissectionIterator(final Tree[] leaves) {
		this(leaves, 0, leaves.length);
	}

	private DissectionIterator(final Tree[] leaves, final int begin, final int end) {
		this.leaves = leaves;
		this.begin = begin;
		this.end = end;
		sep = begin + 1;
		left = buildIterator(begin, sep);
		right = buildIterator(sep, end);
		leftTree = left.next();
	}

	private Iterator<Tree> buildIterator(final int begin, final int end) {
		if (end - begin == 1) {
			return new SingleElementIterator<Tree>(leaves[begin]);
		} else {
			return new DissectionIterator(leaves, begin, end);
		}
	}

	@Override
	public boolean hasNext() {
		return right.hasNext() || left.hasNext() || sep < end - 1;
	}

	@Override
	public Tree next() {
		if (right.hasNext()) {
			final Tree rightTree = right.next();
			if (end < leaves.length) {
				return new InternalVTree((VTree) leftTree, (VTree) rightTree);
			} else {
				return new InternalAVTree((VTree) leftTree, (AVTree) rightTree);
			}
		} else if (left.hasNext()) {
			leftTree = left.next();
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
	public Iterator<Tree> iterator() {
		return this;
	}

}
