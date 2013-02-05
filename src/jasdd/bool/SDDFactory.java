package jasdd.bool;

import jasdd.logic.Literal;
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

	public ConstantSDD createFalse() {
		return createConstant(false);
	}

	public ConstantSDD createTrue() {
		return createConstant(true);
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

	/**
	 * Base factory method for Elements.
	 * 
	 * @param prime the prime SDD
	 * @param sub the sub SDD
	 * @return the cached instance of the Element
	 */
	public Element createElement(final SDD prime, final SDD sub) {
		return cache(new Element(prime, sub));
	}

	public SDD createLiteral(final LiteralSDD sdd) {
		return cache(sdd);
	}

	public SDD createDecomposition(final DecompositionSDD sdd) {
		return cache(sdd);
	}

	// Factory methods corresponding to Element constructors

	public Element createElement(final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		return createElement(createLiteral(v1, s1), createLiteral(v2, s2));
	}

	public Element createElement(final Variable v1, final Variable v2) {
		return createElement(v1, true, v2, true);
	}

	public Element createElement(final Variable v1, final boolean s2) {
		return createElement(v1, true, s2);
	}

	public Element createElement(final boolean s1, final Variable v2) {
		return createElement(s1, v2, true);
	}

	public Element createElement(final boolean s1, final Literal l2) {
		return createElement(s1, l2.getVariable(), l2.getSign());
	}

	public Element createElement(final Variable v1, final boolean s1, final Variable v2) {
		return createElement(v1, s1, v2, true);
	}

	public Element createElement(final Variable v1, final Variable v2, final boolean s2) {
		return createElement(v1, true, v2, s2);
	}

	public Element createElement(final Variable v1, final boolean s1, final boolean s2) {
		return createElement(createLiteral(v1, s1), createConstant(s2));
	}

	public Element createElement(final boolean s1, final Variable v2, final boolean s2) {
		return createElement(createConstant(s1), createLiteral(v2, s2));
	}

	public Element createElement(final boolean s1, final boolean s2) {
		return createElement(createConstant(s1), createConstant(s2));
	}

	public Element createElement(final SDD prime, final boolean s2) {
		return createElement(prime, createConstant(s2));
	}

	public Element createElement(final boolean s1, final SDD sub) {
		return createElement(createConstant(s1), sub);
	}

	public Element createElement(final SDD prime, final Variable v2, final boolean s2) {
		return createElement(prime, createLiteral(v2, s2));
	}

	public Element createElement(final SDD prime, final Variable v2) {
		return createElement(prime, v2, true);
	}

	public Element createElement(final Variable v1, final boolean s1, final SDD sub) {
		return createElement(createLiteral(v1, s1), sub);
	}

	public Element createElement(final Variable v1, final SDD sub) {
		return createElement(v1, true, sub);
	}

	public Element createElement(final Literal l1, boolean s2) {
		return createElement(l1.getVariable(), l1.getSign(), s2);
	}

	public Element createElement(final Literal l1, final Literal l2) {
		return createElement(l1.getVariable(), l1.getSign(), l2.getVariable(), l2.getSign());
	}

	// Shannon decompositions

	public Element[] shannon(final Variable v, final Variable v1, final Variable v2, final boolean s2) {
		return shannon(v, v1, true, v2, s2);
	}

	public Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2) {
		return shannon(v, v1, s1, v2, true);
	}

	public Element[] shannon(final Variable v, final Variable v1, final Variable v2) {
		return shannon(v, v1, true, v2, true);
	}

	public Element[] shannon(final Variable v, final Variable v1, final boolean s1, final Variable v2, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = createElement(v, true, v1, s1);
		elems[1] = createElement(v, false, v2, s2);
		return elems;
	}

	public Element[] shannon(final Variable v, final Variable v1, final boolean s2) {
		return shannon(v, v1, true, s2);
	}

	public Element[] shannon(final Variable v, final Variable v1, final boolean s1, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = createElement(v, true, v1, s1);
		elems[1] = createElement(v, false, s2);
		return elems;
	}

	public Element[] shannon(final Variable v, final boolean s1, final Variable v2) {
		return shannon(v, s1, v2, true);
	}

	public Element[] shannon(final Variable v, final boolean s1, final Variable v2, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = createElement(v, true, s1);
		elems[1] = createElement(v, false, v2, s2);
		return elems;
	}

	public Element[] shannon(final Variable v, final boolean s1, final boolean s2) {
		final Element[] elems = new Element[2];
		elems[0] = createElement(v, true, s1);
		elems[1] = createElement(v, false, s2);
		return elems;
	}

	// Additional helper factory methods for Literal SDDs

	public LiteralSDD createLiteral(final int index, final boolean sign) {
		return createLiteral(new Variable(index), sign);
	}

	public LiteralSDD createLiteral(final int index) {
		return createLiteral(new Variable(index), true);
	}

	public LiteralSDD createLiteral(final Variable variable) {
		return createLiteral(variable, true);
	}

}
