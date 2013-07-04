package jasdd.bool;

import jasdd.JASDD;
import jasdd.logic.Disjunction;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.visitor.SDDVisitor;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.Rotatable;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * SDD for a (X,Y)-decomposition of a boolean function.
 *
 * @author Ricardo Herrmann
 */
public class DecompositionSDD extends AbstractSDD implements Rotatable<SDD> {

	private InternalVTree vtree;
	private final List<Element> elements = new ArrayList<Element>();

	/* package*/ DecompositionSDD(final VTree node, final Element... elements) {
		this((InternalVTree) node, elements);
	}

	private DecompositionSDD(final InternalVTree node, final Element... elements) {
		this.vtree = node;
		for (final Element element : elements) {
			// fixVSubTree(element.getPrime(), node.getLeft());
			// fixVSubTree(element.getSub(), node.getRight());
			this.elements.add(element);
		}
	}

	@Override
	public int size() {
		return size(new HashSet<Element>());
	}

	// Should be package-level when ASDD resides in the same package
	public int size(final Set<Element> visited) {
		int sum = 0;
		for (final Element elem : elements) {
			if (!visited.contains(elem)) {
				sum++;
				visited.add(elem);
				final SDD prime = elem.getPrime();
				if (prime instanceof DecompositionSDD) {
					sum += ((DecompositionSDD) prime).size(visited);
				}
				final SDD sub = elem.getSub();
				if (sub instanceof DecompositionSDD) {
					sum += ((DecompositionSDD) sub).size(visited);
				}
			}
		}
		return sum;
	}

	public InternalVTree getVTree() {
		return vtree;
	}

	/* package */ void setVTree(final InternalVTree vtree) {
		this.vtree = vtree;
	}

	public Collection<Element> getElements() {
		return elements;
	}

	public boolean isStronglyDeterministic() {
		for (final Element i : elements) {
			for (final Element j : elements) {
				if (!i.equals(j)) {
					final SDD pi = i.getPrime();
					final SDD pj = j.getPrime();
					if (!pi.and(pj).isFalse()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public StringBuilder toStringBuilder() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[").append(vtree.toStringBuilder()).append(", (");
		boolean first = true;
		for (final Element elem : elements) {
			if (!first) {
				sb.append(" \\/ ");
			}
			first = false;
			sb.append(elem.toStringBuilder());
		}
		sb.append(")]");
		return sb;
	}

	@Override
	public boolean isTautology() {
		for (final Element element : elements) {
			if (element.isTautology()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isUnsatisfiable() {
		for (final Element element : elements) {
			if (!element.isUnsatisfiable()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isFalse() {
		// TODO Auto-generated method stub
		return false;
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Variable v, final boolean sign) {
		return buildNormalized(vtree, new Literal(v, sign));
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Variable v) {
		return buildNormalized(vtree, new Literal(v));
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final int index) {
		return buildNormalized(vtree, Literal.from(index));
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Literal lit) {
		final Variable v = lit.getVariable();
		// Case 1: left vtree is the variable itself
		final VTree left = vtree.getLeft();
		if (left instanceof VariableLeaf) {
			final Variable treeVar = ((VariableLeaf) left).getVariable();
			if (treeVar.equals(v)) {
				return JASDD.createDecomposition(vtree, JASDD.shannon(v, lit.getSign(), !lit.getSign()));
			}
		}
		// Case 2: right vtree is the variable itself
		final VTree right = vtree.getRight();
		if (right instanceof VariableLeaf) {
			final Variable treeVar = ((VariableLeaf) right).getVariable();
			if (treeVar.equals(v)) {
				return JASDD.createDecomposition(vtree, JASDD.createElement(true, lit));
			}
		}
		// Case 3: variable is in the left vtree
		if (left.variables().contains(v)) {
			final SDD top = buildNormalized((InternalVTree) left, lit);
			final SDD bottom = buildNormalized((InternalVTree) left, lit.opposite());
			return JASDD.createDecomposition(vtree, JASDD.createElement(top, true), JASDD.createElement(bottom, false));
		}
		// Case 4: variable is in the right vtree
		if (right.variables().contains(v)) {
			final SDD sub = buildNormalized((InternalVTree) right, lit);
			return JASDD.createDecomposition(vtree, JASDD.createElement(true, sub));
		}
		// Case 5: there's no case 5. What went wrong?
		throw new IllegalArgumentException("Variable " + v.toString() + " is not in the vtree " + vtree.toString());
	}

	@Override
	public SDD trimmed() {
		final List<Element> elements = new ArrayList<Element>();
		for (final Element element : getElements()) {
			elements.add(element.trimmed());
		}
		final ConstantSDD trueNode = JASDD.createTrue();
		final ConstantSDD falseNode = JASDD.createFalse();
		if (elements.size() == 1 && elements.get(0).getPrime().equals(trueNode)) {
			return elements.get(0).getSub();
		} else if (elements.size() == 2 && elements.get(0).getSub().equals(trueNode) && elements.get(1).getSub().equals(falseNode)) {
			return elements.get(0).getPrime();
		} else {
			final Element[] elems = new Element[elements.size()];
			elements.toArray(elems);
			return JASDD.createDecomposition(getVTree(), elems);
		}
	}

	@Override
	public Collection<Element> expansion() {
		return Collections.unmodifiableCollection(elements);
	}

	@Override
	public boolean isConsistent() {
		// TODO Auto-generated method stub
		return true;
	}

	private Integer hashCode;

	@Override
	public int hashCode() {
		if (hashCode == null) {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((elements == null) ? 0 : elements.hashCode());
			result = prime * result + ((vtree == null) ? 0 : vtree.hashCode());
			hashCode = result;
		}
		return hashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DecompositionSDD other = (DecompositionSDD) obj;
		if (elements == null) {
			if (other.elements != null) {
				return false;
			}
		} else {
			final boolean sameSize = other.elements != null && elements.size() == other.elements.size();
			if (!sameSize) {
				return false;
			}
			final boolean sameSet = getElementsSet().equals(other.getElementsSet());
			if (!sameSet) {
				return false;
			}
		}
		if (vtree == null) {
			if (other.vtree != null) {
				return false;
			}
		} else if (!vtree.equals(other.vtree)) {
			return false;
		}
		return true;
	}

	private Set<Element> elementsSet;

	private static int elementsSetCacheHit, elementsSetCacheMiss;

	private Set<Element> getElementsSet() {
		if (elementsSet == null) {
			elementsSetCacheMiss++;
			elementsSet = new HashSet<Element>(elements);
		} else {
			elementsSetCacheHit++;
		}
		return elementsSet;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	/**
	 * For debugging purposes only: the model should not rely on the visualization package.
	 */
	@Override
	public void dump() {
		try {
			GraphvizDumper.setOutput(new PrintStream("sdd.dot"));
		} catch (final FileNotFoundException e) {
		}
		GraphvizDumper.dump(this);
	}

	@Override
	public void accept(final SDDVisitor visitor) {
		if (visitor.visit(this)) {
			for (final Element elem : getElements()) {
				elem.accept(visitor);
			}
			visitor.postVisit(this);
		}
	}

	@Override
	public Formula getFormula() {
		final Collection<Element> elems = getElements();
		final List<Formula> formulas = new ArrayList<Formula>(elems.size());
		for (final Element elem : elems) {
			formulas.add(elem.getFormula());
		}
		return new Disjunction(formulas);
	}

	@Override
	public boolean eval(final Set<Variable> trueLiterals) {
		for (final Element elem : getElements()) {
			if (elem.eval(trueLiterals)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canRotateLeft() {
		return getVTree().canRotateLeft();
	}

	@Override
	public boolean canRotateRight() {
		return getVTree().canRotateRight();
	}

	@Override
	public SDD rotateLeft() {
		if (!canRotateLeft()) {
			throw new UnsupportedOperationException("SDD cannot be further rotated left");
		}

		// Rotated vtree references
		final InternalVTree rotatedVTree = getVTree().rotateLeft();
		final InternalVTree leftVTree = (InternalVTree) rotatedVTree.getLeft();

		// Accumulated partition for subs in the rotated decomposition
		final Map<SDD, SDD> cache = new HashMap<SDD, SDD>();

		for (final Element elemA : getElements()) {
			final SDD primeA = normalizeIfTerminal(elemA.getPrime(), leftVTree);
			// Lazily normalize terminal subs
			final DecompositionSDD sub = normalizeIfTerminal(elemA.getSub(), leftVTree);
			for (final Element elemB : sub.getElements()) {
				final SDD primeB = normalizeIfTerminal(elemB.getPrime(), leftVTree);
				final SDD subB = elemB.getSub();
				// Normalize for rotated vtree before conjunction
				// TODO: avoid construction of negative partition
				final DecompositionSDD a = nestDecomposition(rotatedVTree, primeA);
				final DecompositionSDD b = nestDecomposition(rotatedVTree, primeB);
				final SDD prime = a.and(b);
				if (prime.isConsistent()) {
					for (final Element e : ((DecompositionSDD) prime).getElements()) {
						if (e.getSub().equals(JASDD.createTrue())) {
							final SDD partition = e.getPrime();
							// Compress
							if (cache.containsKey(subB)) {
								final SDD cachedSub = cache.get(subB);
								final SDD newPart = cachedSub.or(partition);
								cache.put(subB, newPart);
							} else {
								cache.put(subB, partition);
							}
							break;
						}
					}
				}
			}
		}

		final int size = cache.size();
		final Entry<SDD, SDD> first = cache.entrySet().iterator().next();
		// Apply light trimming if possible
		if (size == 1 && first.getValue().equals(JASDD.createTrue()) && first.getKey() instanceof ConstantSDD) {
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
			final DecompositionSDD sdd = JASDD.createDecomposition(rotatedVTree, elems);
			return sdd;
		}
	}

	private DecompositionSDD nestDecomposition(final InternalVTree vtree, final SDD sdd) {
		return JASDD.createDecomposition(vtree, JASDD.createElement(sdd, true), JASDD.createElement(sdd.not(), false));
	}

	public DecompositionSDD normalizeIfTerminal(final SDD sdd, final InternalVTree vtree) {
		if (sdd instanceof ConstantSDD) {
			// Lazily normalize terminal subs
			return JASDD.createDecomposition(vtree, JASDD.createElement(true, sdd));
		} else if (sdd instanceof LiteralSDD) {
			return JASDD.buildNormalized(vtree, ((LiteralSDD) sdd).getLiteral());
		} else {
			final DecompositionSDD decomp = (DecompositionSDD) sdd;
			if (vtree.getLeft().equals(decomp.getVTree())) {
				return nestDecomposition(vtree, decomp);
			} else if (vtree.getRight().equals(decomp.getVTree())) {
				return JASDD.createDecomposition(vtree, JASDD.createElement(true, decomp));
			} else {
				return decomp;
			}
		}
	}

	@Override
	public DecompositionSDD rotateRight() {
		if (!canRotateRight()) {
			throw new UnsupportedOperationException("SDD cannot be further rotated right");
		}
		return null;
	}

	@Override
	public DecompositionSDD not() {
		final Element[] elems = new Element[getElements().size()];
		int i = 0;
		for (final Element elem : getElements()) {
			elems[i++] = elem.not();
		}
		return JASDD.createDecomposition(getVTree(), elems);
	}

}
