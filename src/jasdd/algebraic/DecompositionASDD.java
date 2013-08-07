package jasdd.algebraic;

import jasdd.JASDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.bool.OperatorApplication;
import jasdd.bool.OrOperator;
import jasdd.bool.SDD;
import jasdd.logic.Conjunction;
import jasdd.logic.Constant;
import jasdd.logic.Disjunction;
import jasdd.logic.Formula;
import jasdd.logic.Variable;
import jasdd.util.CloneableIterator;
import jasdd.visitor.ASDDVisitor;
import jasdd.vtree.Direction;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.Rotatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The algebraic equivalent of a decomposition SDD.
 *
 * @author Ricardo Herrmann
 */
public class DecompositionASDD<T> implements ASDD<T>, Rotatable<ASDD<T>> {

	private final InternalAVTree avtree;

	private final List<AlgebraicElement<T>> elements;

	/* package */ DecompositionASDD(final InternalAVTree avtree, final AlgebraicElement<T>... elements) {
		this.avtree = avtree;
		this.elements = new ArrayList<AlgebraicElement<T>>(elements.length);
		for (final AlgebraicElement<T> element : elements) {
			addElement(element);
		}
	}

	// TODO: avoid mutation, specially now that there's a constructor that creates a immutable list of elements.
	private void addElement(AlgebraicElement<T> newElement) {
		// Apply compression if possible
		for (final AlgebraicElement<T> element : elements) {
			final ASDD<T> sub = element.getSub();
			if (sub.equals(newElement.getSub())) {
				final SDD newPrime = new OperatorApplication(element.getPrime(), newElement.getPrime(), new OrOperator()).apply();
				newElement = JASDD.createElement(newPrime, sub);
				elements.remove(element);
				break;
			}
		}
		elements.add(newElement);
	}

	public List<AlgebraicElement<T>> getElements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public InternalAVTree getTree() {
		return avtree;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public int size() {
		return size(new HashSet<Element>(), new HashSet<AlgebraicElement<T>>());
	}

	private int size(final Set<Element> visited, final Set<AlgebraicElement<T>> algebraicVisited) {
		int sum = 0;
		for (final AlgebraicElement<T> elem : elements) {
			if (!algebraicVisited.contains(elem)) {
				sum++;
				algebraicVisited.add(elem);
				final SDD prime = elem.getPrime();
				if (prime instanceof DecompositionSDD) {
					sum += ((DecompositionSDD) prime).size(visited);
				}
				final ASDD<T> sub = elem.getSub();
				if (sub instanceof DecompositionASDD<?>) {
					sum += ((DecompositionASDD<T>) sub).size(visited, algebraicVisited);
				}
			}
		}
		return sum;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avtree == null) ? 0 : avtree.hashCode());
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		return result;
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
		@SuppressWarnings("unchecked")
		final
		DecompositionASDD<T> other = (DecompositionASDD<T>) obj;
		if (avtree == null) {
			if (other.avtree != null) {
				return false;
			}
		} else if (!avtree.equals(other.avtree)) {
			return false;
		}
		if (elements == null) {
			if (other.elements != null) {
				return false;
			}
		} else if (!elements.equals(other.elements)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return toStringBuilder().toString();
	}

	@Override
	public StringBuilder toStringBuilder() {
		final StringBuilder sb = new StringBuilder();
		sb.append("[").append(avtree.toStringBuilder()).append(", (");
		boolean first = true;
		for (final AlgebraicElement<T> elem : elements) {
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
	public void accept(final ASDDVisitor<T> visitor) {
		if (visitor.visit(this)) {
			for (final AlgebraicElement<T> elem : getElements()) {
				elem.accept(visitor);
			}
			visitor.postVisit(this);
		}
	}

	@Override
	public ASDD<T> trimmed() {
		final List<AlgebraicElement<T>> elements = new ArrayList<AlgebraicElement<T>>();
		for (final AlgebraicElement<T> element : getElements()) {
			elements.add(element.trimmed());
		}
		final AlgebraicElement<T> first = elements.get(0);
		if (elements.size() == 1 && first.getPrime().isTautology()) {
			return first.getSub().trimmed();
		} else {
			@SuppressWarnings("unchecked")
			final AlgebraicElement<T>[] elems = new AlgebraicElement[elements.size()];
			elements.toArray(elems);
			return new DecompositionASDD<T>(getTree(), elems);
		}
	}

	@Override
	public Set<T> terminals() {
		final Set<T> terminals = new HashSet<T>();
		for (final AlgebraicElement<T> element : getElements()) {
			terminals.addAll(element.getSub().terminals());
		}
		return terminals;
	}

	/**
	 * Extract a map from every algebraic terminal to the boolean formula that
	 * evaluates to each terminal.
	 *
	 * @return a map from terminals to formulas
	 */
	public Map<T, Formula> extractFunction() {
		return extractFunction(new Constant(true), new HashMap<T, Formula>());
	}

	public Map<T, Formula> extractFunction(final Formula partition, Map<T, Formula> partial) {
		for (final AlgebraicElement<T> elem : getElements()) {
			final Formula refinement = elem.getPrime().getFormula();
			final Formula primeFormula = Conjunction.from(Arrays.asList(partition, refinement)).trim();
			final ASDD<T> sub = elem.getSub();
			if (sub instanceof AlgebraicTerminal) {
				final T value = ((AlgebraicTerminal<T>) sub).getValue();
				final Formula currentFormula = partial.get(value);
				if (null == currentFormula) {
					partial.put(value, primeFormula);
				} else {
					partial.put(value, Disjunction.from(Arrays.asList(currentFormula, primeFormula)).trim());
				}
			} else {
				partial = ((DecompositionASDD<T>) sub).extractFunction(primeFormula, partial);
			}
		}
		return partial;
	}

	@Override
	public T eval(final Set<Variable> trueLiterals) {
		for (final AlgebraicElement<T> elem : getElements()) {
			if (elem.getPrime().eval(trueLiterals)) {
				return elem.getSub().eval(trueLiterals);
			}
		}
		return null;
	}

	// Rotation ////////////////////////////////////////

	@Override
	public boolean canRotateLeft() {
		return getTree().canRotateLeft();
	}

	@Override
	public boolean canRotateRight() {
		return getTree().canRotateRight();
	}

	@Override
	public boolean canRotateLeft(final Direction... path) {
		return getTree().canRotateLeft(path);
	}

	@Override
	public boolean canRotateRight(final Direction... path) {
		return getTree().canRotateRight(path);
	}

	@Override
	public boolean canRotateLeft(final Iterator<Direction> path) {
		return getTree().canRotateLeft(path);
	}

	@Override
	public boolean canRotateRight(final Iterator<Direction> path) {
		return getTree().canRotateRight(path);
	}

	@Override
	public ASDD<T> rotateLeft() {
		if (!canRotateLeft()) {
			throw new UnsupportedOperationException("ASDD cannot be further rotated left");
		}

		// Rotated vtree references
		final InternalAVTree rotatedAVTree = (InternalAVTree) getTree().rotateLeft();
		final InternalVTree leftVTree = (InternalVTree) rotatedAVTree.getLeft();

		// Accumulated partition for subs in the rotated decomposition
		final CompressedAlgebraicPartition<T> partition = new CompressedAlgebraicPartition<T>();

		for (final AlgebraicElement<T> elem : getElements()) {
			final SDD a = elem.getPrime().nest(leftVTree);
			final ASDD<T> sub = elem.getSub();
			if (!sub.isTerminal()) {
				for (final AlgebraicElement<T> subElem : ((DecompositionASDD<T>) sub).getElements()) {
					final SDD b = subElem.getPrime().nest(leftVTree);
					final ASDD<T> c = subElem.getSub();
					final SDD prime = a.and(b);
					partition.add(prime, c);
				}
			}
		}
		return partition.decomposition(rotatedAVTree);
	}

	@Override
	public ASDD<T> rotateRight() {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ASDD<T> rotateLeft(final Direction... path) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ASDD<T> rotateRight(final Direction... path) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ASDD<T> rotateLeft(final CloneableIterator<Direction> path) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public ASDD<T> rotateRight(final CloneableIterator<Direction> path) {
		// TODO
		throw new UnsupportedOperationException();
	}

}
