package jasdd.algebraic;

import jasdd.bool.SDD;
import jasdd.bool.SDDFactory;
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
public class ASDDFactory {

	private static ASDDFactory instance;

	private Map<ASDD, ASDD> cache = new HashMap<ASDD, ASDD>();

	private final Map<AlgebraicElement, AlgebraicElement> elementCache = new HashMap<AlgebraicElement, AlgebraicElement>();

	public static ASDDFactory getInstance() {
		if (null == instance) {
			instance = new ASDDFactory();
		}
		return instance;
	}

	private ASDD cache(final ASDD asdd) {
		if (cache.containsKey(asdd)) {
			return cache.get(asdd);
		} else {
			cache.put(asdd, asdd);
			return asdd;
		}
	}

	private AlgebraicElement cache(final AlgebraicElement elem) {
		if (elementCache.containsKey(elem)) {
			return elementCache.get(elem);
		} else {
			elementCache.put(elem, elem);
			return elem;
		}
	}

	public DecompositionASDD createDecomposition(final InternalAVTree avtree, final AlgebraicElement... elements) {
		return (DecompositionASDD) cache(new DecompositionASDD(avtree, elements));
	}

	public AlgebraicTerminal createTerminal(final Object value) {
		return (AlgebraicTerminal) cache(new AlgebraicTerminal(value));
	}

	public AlgebraicElement createElement(final SDD prime, final ASDD sub) {
		return cache(new AlgebraicElement(prime, sub));
	}

	// Auxiliary

	public DecompositionASDD createDecomposition(final InternalAVTree avtree, final List<AlgebraicElement> elements) {
		final AlgebraicElement[] elems = new AlgebraicElement[elements.size()];
		int i = 0;
		for (final AlgebraicElement elem : elements) {
			elems[i++] = elem;
		}
		return createDecomposition(avtree, elems);
	}

	public AlgebraicElement createElement(final ASDD sub) {
		return createElement(SDDFactory.getInstance().createTrue(), sub);
	}

	public AlgebraicElement createElement(final Variable v1, final boolean s1, final ASDD sub) {
		return createElement(SDDFactory.getInstance().createLiteral(v1, s1), sub);
	}

	public AlgebraicElement createElement(final Variable v1, final ASDD sub) {
		return createElement(v1, true, sub);
	}

}
