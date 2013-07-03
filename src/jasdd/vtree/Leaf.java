package jasdd.vtree;

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

}
