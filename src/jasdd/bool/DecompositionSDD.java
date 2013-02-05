package jasdd.bool;

import jasdd.logic.Literal;
import jasdd.logic.Variable;
import jasdd.visitor.SDDVisitor;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * SDD for a (X,Y)-decomposition of a boolean function.
 * 
 * @author Ricardo Herrmann
 */
public class DecompositionSDD extends AbstractSDD {

	private InternalVTree vtree;
	private List<Element> elements = new ArrayList<Element>();

	private DecompositionSDD(final DecompositionSDD sdd) {
		this.vtree = sdd.getVTree();
		this.elements = (List<Element>) sdd.getElements();
		// TODO: Deep copy
	}

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

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Variable v, boolean sign) {
		return buildNormalized(vtree, new Literal(v, sign));
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Variable v) {
		return buildNormalized(vtree, new Literal(v));
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final int index) {
		return buildNormalized(vtree, Literal.from(index));
	}

	public static DecompositionSDD buildNormalized(final InternalVTree vtree, final Literal lit) {
		final SDDFactory factory = SDDFactory.getInstance();
		final Variable v = lit.getVariable();
		// Case 1: left vtree is the variable itself
		final VTree left = vtree.getLeft();
		if (left instanceof VariableLeaf) {
			final Variable treeVar = ((VariableLeaf) left).getVariable();
			if (treeVar.equals(v)) {
				return new DecompositionSDD(vtree, Element.shannon(v, lit.getSign(), !lit.getSign()));
			}
		}
		// Case 2: right vtree is the variable itself
		final VTree right = vtree.getRight();
		if (right instanceof VariableLeaf) {
			final Variable treeVar = ((VariableLeaf) right).getVariable();
			if (treeVar.equals(v)) {
				return new DecompositionSDD(vtree, factory.createElement(true, lit));
			}
		}
		// Case 3: variable is in the left vtree
		if (left.variables().contains(v)) {
			final SDD top = buildNormalized((InternalVTree) left, lit);
			final SDD bottom = buildNormalized((InternalVTree) left, lit.opposite());
			return new DecompositionSDD(vtree, factory.createElement(top, true), factory.createElement(bottom, false));
		}
		// Case 4: variable is in the right vtree
		if (right.variables().contains(v)) {
			final SDD sub = buildNormalized((InternalVTree) right, lit);
			return new DecompositionSDD(vtree, factory.createElement(true, sub));
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
		final SDDFactory factory = SDDFactory.getInstance();
		final ConstantSDD trueNode = factory.createTrue();
		final ConstantSDD falseNode = factory.createFalse();
		if (elements.size() == 1 && elements.get(0).getPrime().equals(trueNode)) {
			return elements.get(0).getSub().trimmed();
		} else if (elements.size() == 2 && elements.get(0).getSub().equals(trueNode) && elements.get(1).getSub().equals(falseNode)) {
			return elements.get(0).getPrime().trimmed();
		} else {
			final Element[] elems = new Element[elements.size()];
			elements.toArray(elems);
			return new DecompositionSDD(getVTree(), elems);
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
			if (other.vtree != null)
				return false;
		} else if (!vtree.equals(other.vtree))
			return false;
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

}
