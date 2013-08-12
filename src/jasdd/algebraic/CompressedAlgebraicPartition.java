package jasdd.algebraic;

import jasdd.JASDD;
import jasdd.bool.SDD;
import jasdd.vtree.InternalAVTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Compresses an ASDD.
 *
 * @author Ricardo Herrmann
 *
 * @param <T>
 *            type of the ASDD
 */
public class CompressedAlgebraicPartition<T> {

	private final Map<ASDD<T>, SDD> cache = new HashMap<ASDD<T>, SDD>();

	public void add(final SDD prime, final ASDD<T> sub) {
		if (cache.containsKey(sub)) {
			final SDD cachedSub = cache.get(sub);
			final SDD newPrime = cachedSub.or(prime);
			cache.put(sub, newPrime);
		} else {
			cache.put(sub, prime);
		}
	}

	public void add(final Iterable<AlgebraicElement<T>> elements) {
		for (final AlgebraicElement<T> element : elements) {
			final SDD prime = element.getPrime();
			final ASDD<T> sub = element.getSub();
			add(prime, sub);
		}
	}

	public ASDD<T> decomposition(final InternalAVTree avtree) {
		int i = 0;
		@SuppressWarnings("unchecked")
		final AlgebraicElement<T>[] elems = new AlgebraicElement[cache.size()];
		for (final Entry<ASDD<T>, SDD> entry : cache.entrySet()) {
			final ASDD<T> sub = entry.getKey();
			final SDD prime = entry.getValue();
			final AlgebraicElement<T> elem = JASDD.createElement(prime, sub);
			elems[i++] = elem;
		}
		return JASDD.createDecomposition(avtree, elems);
	}

}
