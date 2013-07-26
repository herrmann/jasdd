package jasdd.tools;

import jasdd.util.CloneableIterator;
import jasdd.util.CloneableListIterator;
import jasdd.vtree.Direction;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A path in a binary structure since its root.
 *
 * @author Ricardo Herrmann
 */
public class Path {

	private final List<Direction> path = new LinkedList<Direction>();

	public void add(final Direction... path) {
		this.path.addAll(Arrays.asList(path));
	}

	public Iterator<Direction> iterator() {
		return path.iterator();
	}

	public CloneableIterator<Direction> cloneableIterator() {
		return new CloneableListIterator<Direction>(path);
	}

	public boolean isEmpty() {
		return path.isEmpty();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final Direction dir : path) {
			sb.append(Direction.LEFT == dir ? "L" : "R");
		}
		return sb.toString();
	}

}