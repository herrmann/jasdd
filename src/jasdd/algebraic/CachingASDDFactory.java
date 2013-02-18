package jasdd.algebraic;

import jasdd.bool.SDD;
import jasdd.bool.CachingSDDFactory;
import jasdd.logic.Variable;
import jasdd.vtree.InternalAVTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates and caches different types of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public class CachingASDDFactory implements ASDDFactory {

	private static CachingASDDFactory instance;

	@SuppressWarnings("rawtypes")
	private Map<ASDD, ASDD> cache = new HashMap<ASDD, ASDD>();

	@SuppressWarnings("rawtypes")
	private final Map<AlgebraicElement, AlgebraicElement> elementCache = new HashMap<AlgebraicElement, AlgebraicElement>();

	public static CachingASDDFactory getInstance() {
		if (null == instance) {
			instance = new CachingASDDFactory();
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private <T> ASDD<T> cache(final ASDD<T> asdd) {
		if (cache.containsKey(asdd)) {
			return cache.get(asdd);
		} else {
			cache.put(asdd, asdd);
			return asdd;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> AlgebraicElement<T> cache(final AlgebraicElement<T> elem) {
		if (elementCache.containsKey(elem)) {
			return elementCache.get(elem);
		} else {
			elementCache.put(elem, elem);
			return elem;
		}
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createDecomposition(jasdd.vtree.InternalAVTree, jasdd.algebraic.AlgebraicElement)
	 */
	@Override
	public <T> DecompositionASDD<T> createDecomposition(final InternalAVTree avtree, final AlgebraicElement<T>... elements) {
		return (DecompositionASDD<T>) cache(new DecompositionASDD<T>(avtree, elements));
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createTerminal(T)
	 */
	@Override
	public <T> AlgebraicTerminal<T> createTerminal(final T value) {
		return (AlgebraicTerminal<T>) cache(new AlgebraicTerminal<T>(value));
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createElement(jasdd.bool.SDD, jasdd.algebraic.ASDD)
	 */
	@Override
	public <T> AlgebraicElement<T> createElement(final SDD prime, final ASDD<T> sub) {
		return cache(new AlgebraicElement<T>(prime, sub));
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#shannon(jasdd.logic.Variable, jasdd.algebraic.ASDD, jasdd.algebraic.ASDD)
	 */
	@Override
	public <T> AlgebraicElement<T>[] shannon(final Variable v, final ASDD<T> high, final ASDD<T> low) {
		@SuppressWarnings("unchecked")
		final AlgebraicElement<T>[] elems = new AlgebraicElement[2];
		elems[0] = createElement(v, high);
		elems[1] = createElement(v, false, low);
		return elems;
	}

	// Auxiliary

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createDecomposition(jasdd.vtree.InternalAVTree, java.util.List)
	 */
	@Override
	public <T> DecompositionASDD<T> createDecomposition(final InternalAVTree avtree, final List<AlgebraicElement<T>> elements) {
		@SuppressWarnings("unchecked")
		final AlgebraicElement<T>[] elems = new AlgebraicElement[elements.size()];
		int i = 0;
		for (final AlgebraicElement<T> elem : elements) {
			elems[i++] = elem;
		}
		return createDecomposition(avtree, elems);
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createElement(jasdd.algebraic.ASDD)
	 */
	@Override
	public <T> AlgebraicElement<T> createElement(final ASDD<T> sub) {
		return createElement(CachingSDDFactory.getInstance().createTrue(), sub);
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createElement(jasdd.logic.Variable, boolean, jasdd.algebraic.ASDD)
	 */
	@Override
	public <T> AlgebraicElement<T> createElement(final Variable v1, final boolean s1, final ASDD<T> sub) {
		return createElement(CachingSDDFactory.getInstance().createLiteral(v1, s1), sub);
	}

	/* (non-Javadoc)
	 * @see jasdd.algebraic.ASDDFactory#createElement(jasdd.logic.Variable, jasdd.algebraic.ASDD)
	 */
	@Override
	public <T> AlgebraicElement<T> createElement(final Variable v1, final ASDD<T> sub) {
		return createElement(v1, true, sub);
	}

}
