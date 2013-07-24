package jasdd.bool;

import jasdd.JASDD;
import jasdd.logic.Disjunction;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.util.CloneableArrayIterator;
import jasdd.util.CloneableIterator;
import jasdd.util.Pair;
import jasdd.visitor.SDDVisitor;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.Rotatable;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

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
		final InternalTree<VTree> rotatedVTree = getVTree().rotateLeft();
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
				final DecompositionSDD a = nestDecomposition((InternalVTree) rotatedVTree, primeA);
				final DecompositionSDD b = nestDecomposition((InternalVTree) rotatedVTree, primeB);
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
			final DecompositionSDD sdd = JASDD.createDecomposition((InternalVTree) rotatedVTree, elems);
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
		final List<Pair<SDD, List<Element>>> partitions = new ArrayList<Pair<SDD, List<Element>>>();
		for (final Element element : getElements()) {
			// TODO: normalize on demand
			final DecompositionSDD prime = (DecompositionSDD) element.getPrime();
			final List<Element> partition = new ArrayList<Element>();
			for (final Element primeElement : prime.getElements()) {
				partition.add(primeElement);
			}
			partitions.add(Pair.create(element.getSub(), partition));
		}
		final List<Element> newElements = new ArrayList<Element>();
		final InternalTree<VTree> newVTree = getVTree().rotateRight();
		rightCrossProduct(partitions, partitions.listIterator(0), new Stack<Element>(), newElements, (InternalVTree) newVTree);
		// TODO: avoid copy
		final List<Element> compressed = compress(newElements);
		final Element[] elems = new Element[compressed.size()];
		compressed.toArray(elems);
		return JASDD.createDecomposition((InternalVTree) newVTree, elems);
	}

	/**
	 * Compresses a set of elements, assuming all primes respect the same vtree.
	 *
	 * @param elements the list of SDD elements to compress
	 * @return a list with the compression of the input elements
	 */
	private List<Element> compress(final List<Element> elements) {
		final Map<SDD, SDD> cache = new HashMap<SDD, SDD>();
		for (final Element element : elements) {
			final SDD prime = element.getPrime();
			final SDD sub = element.getSub();
			if (cache.containsKey(sub)) {
				final SDD oldPrime = cache.get(sub);
				final SDD newPrime = oldPrime.or(prime);
				cache.put(sub, newPrime);
			} else {
				cache.put(sub, prime);
			}
		}
		final List<Element> newElements = new ArrayList<Element>(cache.size());
		for (final Entry<SDD, SDD> entry : cache.entrySet()) {
			final SDD sub = entry.getKey();
			final SDD prime = entry.getValue();
			final Element element = JASDD.createElement(prime, sub);
			newElements.add(element);
		}
		return newElements;
	}

	private void rightCrossProduct(final List<Pair<SDD, List<Element>>> partitions, final ListIterator<Pair<SDD, List<Element>>> iter, final Stack<Element> stack, final List<Element> newElements, final InternalVTree newVTree) {
		if (iter.hasNext()) {
			final Pair<SDD, List<Element>> partition = iter.next();
			for (final Element element : partition.getSecond()) {
				stack.push(element);
				rightCrossProduct(partitions, partitions.listIterator(iter.nextIndex()), stack, newElements, newVTree);
				stack.pop();
			}
		} else {
			SDD prime = JASDD.createTrue();
			for (final Element element : stack) {
				if (prime.isConsistent()) {
					prime = prime.and(element.getPrime());
				} else {
					break;
				}
			}
			if (prime.isConsistent()) {
				SDD sub = JASDD.createFalse();
				final Iterator<Pair<SDD, List<Element>>> iterC = partitions.iterator();
				for (final Element element : stack) {
					final SDD b = element.getSub();
					final SDD c = iterC.next().getFirst();
					final DecompositionSDD normB = normalizeIfTerminal(b, (InternalVTree) newVTree.getRight());
					final DecompositionSDD normC = normalizeIfTerminal(c, (InternalVTree) newVTree.getRight());
					final SDD sdd = normB.and(normC);
					sub = sub.or(sdd);
				}
				SDD normA = prime;
				if (!newVTree.getLeft().isLeaf()) {
					normA = normalizeIfTerminal(normA, (InternalVTree) newVTree.getLeft());
				}
				final Element elem = JASDD.createElement(normA, sub);
				newElements.add(elem);
			}
		}
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

	public DecompositionSDD swap() {
		final List<List<Element>> partitions = new ArrayList<List<Element>>();
		for (final Element element : getElements()) {
			final List<Element> partition = new ArrayList<Element>(2);
			partition.add(element);
			partition.add(JASDD.createElement(JASDD.createFalse(), element.getSub().not()));
			partitions.add(partition);
		}
		final List<Element> newElements = new ArrayList<Element>();
		swapCrossProduct(partitions, partitions.listIterator(0), new Stack<Element>(), newElements);
		// TODO: avoid copy
		final Element[] elems = new Element[newElements.size()];
		newElements.toArray(elems);
		return JASDD.createDecomposition(getVTree().swap(), elems);
	}

	private void swapCrossProduct(final List<List<Element>> partitions, final ListIterator<List<Element>> iter, final Stack<Element> stack, final List<Element> newElements) {
		if (iter.hasNext()) {
			final List<Element> partition = iter.next();
			for (final Element element : partition) {
				stack.push(element);
				swapCrossProduct(partitions, partitions.listIterator(iter.nextIndex()), stack, newElements);
				stack.pop();
			}
		} else {
			SDD sddSub = JASDD.createTrue();
			for (final Element part : stack) {
				if (sddSub.isConsistent()) {
					sddSub = sddSub.and(part.getSub());
				} else {
					break;
				}
			}
			if (sddSub.isConsistent()) {
				SDD sddPrime = JASDD.createFalse();
				for (final Element part : stack) {
					final SDD prime = part.getPrime();
					if (prime.isConsistent()) {
						sddPrime = sddPrime.or(prime);
					}
				}
				newElements.add(JASDD.createElement(sddSub, sddPrime));
			}
		}
	}

	@Override
	public boolean canRotateLeft(final Iterator<Direction> path) {
		return getVTree().canRotateLeft(path);
	}

	@Override
	public boolean canRotateRight(final Iterator<Direction> path) {
		return getVTree().canRotateRight(path);
	}

	public SDD rotate(final Direction operation, final CloneableIterator<Direction> path) {
		if (path.hasNext()) {
			final CloneableIterator<Direction> originalPath = path.clone();
			final Direction direction = path.next();
			final List<Element> newElements = new ArrayList<Element>();
			if (Direction.LEFT == direction) {
				for (final Element element : getElements()) {
					SDD prime = element.getPrime();
					if (prime instanceof DecompositionSDD) {
						prime = ((DecompositionSDD) prime).rotate(operation, path.clone());
					}
					final Element elem = JASDD.createElement(prime, element.getSub());
					newElements.add(elem);
				}
			} else if (Direction.RIGHT == direction) {
				for (final Element element : getElements()) {
					SDD sub = element.getSub();
					if (sub instanceof DecompositionSDD) {
						sub = ((DecompositionSDD) sub).rotate(operation, path.clone());
					}
					final Element elem = JASDD.createElement(element.getPrime(), sub);
					newElements.add(elem);
				}
			}
			// TODO: reuse sub-vtrees
			final Element[] elems = new Element[newElements.size()];
			newElements.toArray(elems);
			final InternalVTree newVTree = (InternalVTree) getVTree().rotate(operation, originalPath);
			return JASDD.createDecomposition(newVTree, elems);
		} else {
			if (Direction.LEFT == operation) {
				return rotateLeft();
			} else if (Direction.RIGHT == operation) {
				return rotateRight();
			}
		}
		throw new IllegalStateException();
	}

	@Override
	public SDD rotateLeft(final CloneableIterator<Direction> path) {
		return rotate(Direction.LEFT, path);
	}

	@Override
	public SDD rotateRight(final CloneableIterator<Direction> path) {
		return rotate(Direction.RIGHT, path);
	}

	@Override
	public boolean canRotateLeft(final Direction... path) {
		return canRotateLeft(Arrays.asList(path).iterator());
	}

	@Override
	public boolean canRotateRight(final Direction... path) {
		return canRotateRight(Arrays.asList(path).iterator());
	}

	@Override
	public SDD rotateLeft(final Direction... path) {
		return rotateLeft(CloneableArrayIterator.build(path));
	}

	@Override
	public SDD rotateRight(final Direction... path) {
		return rotateRight(CloneableArrayIterator.build(path));
	}

}
