package jasdd.vtree;

import java.util.Iterator;

/**
 * The common interface for all types of algebraic vtrees.
 *
 * @author Ricardo Herrmann
 */
public interface AVTree extends Tree {

	boolean canSwap(Iterator<Direction> path);

}
