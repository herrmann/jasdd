package jasdd.tools;

/**
 * Type of possible operations over decompositions.
 *
 * @author Ricardo Herrmann
 */
enum Operation {

	ROTATE_LEFT, ROTATE_RIGHT, SWAP;

	@Override
	public String toString() {
		switch (this) {
		case ROTATE_LEFT:
			return "RL";
		case ROTATE_RIGHT:
			return "RR";
		case SWAP:
			return "SW";
		}
		throw new IllegalStateException();
	}

}