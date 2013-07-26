package jasdd.tools;

/**
 * Necessary information for an edge in the decomposition manipulation graph.
 *
 * @author Ricardo Herrmann
 */
public class EdgeInfo {

	private final Operation oper;
	private final Path path;

	public EdgeInfo(final Operation oper, final Path path) {
		this.oper = oper;
		this.path = path;
	}

	public EdgeInfo(final Operation oper) {
		this(oper, new Path());
	}

	public Operation getOper() {
		return oper;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(oper);
		if (!path.isEmpty()) {
			sb.append("(").append(path).append(")");
		}
		return sb.toString();
	}

}
