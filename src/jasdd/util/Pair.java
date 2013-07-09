package jasdd.util;

/**
 * The infamous generic pair of objects, for all kinds of bad software
 * engineering practices, but immutable at least.
 *
 * @author Ricardo Herrmann
 *
 * @param <F> first type
 * @param <S> second type
 */
public class Pair<F, S> {

	private final F first;
	private final S second;

	private Pair(final F first, final S second) {
		this.first = first;
		this.second = second;
	}

	public static <F, S> Pair<F, S> create(final F first, final S second) {
		return new Pair<F, S>(first, second);
	}

	public F getFirst() {
		return first;
	}

	public S getSecond() {
		return second;
	}

	@Override
	public String toString() {
		return "<" + getFirst().toString() + ", " + getFirst().toString() + ">";
	}

}
