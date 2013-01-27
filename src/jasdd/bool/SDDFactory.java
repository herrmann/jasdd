package jasdd.bool;

import jasdd.logic.Variable;
import jasdd.vtree.InternalVTree;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates and caches different types of SDDs.
 * 
 * @author Ricardo Herrmann
 */
public class SDDFactory {
	
	private static SDDFactory instance;

	private Map<SDD, SDD> cache = new HashMap<SDD, SDD>();

	private final Map<Element, Element> elementCache = new HashMap<Element, Element>();

	public static SDDFactory getInstance() {
		if (null == instance) {
			instance = new SDDFactory();
		}
		return instance;
	}

	private SDD cache(final SDD sdd) {
		if (cache.containsKey(sdd)) {
			return cache.get(sdd);
		} else {
			cache.put(sdd, sdd);
			return sdd;
		}
	}

	public ConstantSDD createConstant(final boolean sign) {
		return (ConstantSDD) cache(new ConstantSDD(sign));
	}

	public LiteralSDD createLiteral(final Variable variable, final boolean sign) {
		return (LiteralSDD) cache(new LiteralSDD(variable, sign));
	}

	public DecompositionSDD createDecomposition(final InternalVTree node, final Element... elements) {
		final Element[] elems = new Element[elements.length];
		for (int i = 0; i < elems.length; i++) {
			elems[i] = cache(elements[i]);
		}
		return (DecompositionSDD) cache(new DecompositionSDD(node, elems));
	}

	private Element cache(final Element elem) {
		if (elementCache.containsKey(elem)) {
			return elementCache.get(elem);
		} else {
			elementCache.put(elem, elem);
			return elem;
		}
	}

	public Element createElement(final SDD prime, final SDD sub) {
		return cache(new Element(prime, sub));
	}

	public SDD createLiteral(final LiteralSDD sdd) {
		return cache(sdd);
	}

	public SDD createDecomposition(final DecompositionSDD sdd) {
		return cache(sdd);
	}

}
