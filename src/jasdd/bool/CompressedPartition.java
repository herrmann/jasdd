package jasdd.bool;

import jasdd.JASDD;
import jasdd.vtree.InternalVTree;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Compresses an SDD.
 *
 * @author Ricardo Herrmann
 */
public class CompressedPartition {

	private final Map<SDD, SDD> cache = new HashMap<SDD, SDD>();

	public void add(final SDD prime, final SDD sub) {
		if (cache.containsKey(sub)) {
			final SDD cachedSub = cache.get(sub);
			final SDD newPrime = cachedSub.or(prime);
			cache.put(sub, newPrime);
		} else {
			cache.put(sub, prime);
		}
	}

	public SDD decomposition(final InternalVTree vtree) {
		final int size = cache.size();
		final Entry<SDD, SDD> first = cache.entrySet().iterator().next();
		// Apply light trimming if possible
		if (size == 1 && first.getValue().isTautology() && first.getKey() instanceof ConstantSDD) {
			return JASDD.createConstant(((ConstantSDD) first.getKey()).getSign());
		} else {
			int i = 0;
			final Element[] elems = new Element[size];
			for (final Entry<SDD, SDD> entry : cache.entrySet()) {
				final SDD sub = entry.getKey();
				final SDD prime = entry.getValue();
				final Element elem = JASDD.createElement(prime, sub);
				elems[i++] = elem;
			}
			return JASDD.createDecomposition(vtree, elems);
		}
	}

	public void add(final Iterable<Element> elements) {
		for (final Element element : elements) {
			final SDD prime = element.getPrime();
			final SDD sub = element.getSub();
			add(prime, sub);
		}
	}

}
