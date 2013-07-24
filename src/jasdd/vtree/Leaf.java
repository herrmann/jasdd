package jasdd.vtree;

import jasdd.util.CloneableIterator;

import java.util.Iterator;

/**
 * Type for denoting tree leaves.
 *
 * @author Ricardo Herrmann
 */
public abstract class Leaf implements Tree {

	@Override
	public boolean canRotateLeft() {
		return false;
	}

	@Override
	public boolean canRotateRight() {
		return false;
	}

	@Override
	public boolean canRotateLeft(final Iterator<Direction> path) {
		return false;
	}

	@Override
	public boolean canRotateRight(final Iterator<Direction> path) {
		return false;
	}

	@Override
	public Tree rotateLeft() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tree rotateRight() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tree rotateLeft(final CloneableIterator<Direction> path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tree rotateRight(final CloneableIterator<Direction> path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canRotateLeft(final Direction... path) {
		return false;
	}

	@Override
	public boolean canRotateRight(final Direction... path) {
		return false;
	}

	@Override
	public Tree rotateLeft(final Direction... path) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tree rotateRight(final Direction... path) {
		throw new UnsupportedOperationException();
	}

}
