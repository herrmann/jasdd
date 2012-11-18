package jsdd;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import jsdd.viz.GraphvizDumper;

/**
 * SDD for a (X,Y)-decomposition of a boolean function.
 * 
 * @author Ricardo Herrmann
 */
public class DecompositionSDD extends AbstractSDD {

	private InternalNode vtree;
	private List<Element> elements = new ArrayList<Element>();

	public DecompositionSDD(final DecompositionSDD sdd) {
		this.vtree = sdd.getVTree();
		this.elements = (List<Element>) sdd.getElements();
		// TODO: Deep copy
	}

	public DecompositionSDD(final VTree node, final Element... elements) {
		this((InternalNode) node, elements);
	}

	public DecompositionSDD(final InternalNode node, final Element... elements) {
		this.vtree = node;
		for (final Element element : elements) {
			element.addParent(this);
			// fixVSubTree(element.getPrime(), node.getLeft());
			// fixVSubTree(element.getSub(), node.getRight());
			this.elements.add(element);
		}
	}

	private void fixVSubTree(final SDD sdd, final VTree correctNode) {
		if (sdd instanceof DecompositionSDD) {
			final VTree vtree = sdd.getVTree();
			if (vtree != null) {
				if (!vtree.equals(correctNode)) {
					throw new IllegalArgumentException("Decomposition element doesn't respect the decomposition's vtree");
				}
			} else {
				((DecompositionSDD) sdd).setVTree((InternalNode) correctNode);
			}
		}
	}

	@Override
	public InternalNode getVTree() {
		return vtree;
	}

	private void setVTree(final InternalNode vtree) {
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
		if (isTautology()) {
			sb.append(TRUE);
		} else {
			boolean first = true;
			for (final Element element : elements) {
				if (!element.isUnsatisfiable()) {
					if (!first) {
						sb.append(" \\/ ");
					}
					first = false;
					sb.append(element.toStringBuilder());
				}
			}
			if (first) {
				sb.append(FALSE);
			}
		}
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

	@Override
	public SDD apply(final ConstantSDD sdd, final BooleanOperator op) {
		return apply(this, op);
	}

	@Override
	public SDD apply(final LiteralSDD sdd, final BooleanOperator op) {
		final Variable variable = sdd.getLiteral().getVariable();
		DecompositionSDD decomp;
		if (getVTree().getLeft().variables().contains(variable)) {
			final Literal literal = sdd.getLiteral();
			decomp = new DecompositionSDD(getVTree(),
					new Element(variable, literal.getSign()),
					new Element(variable, false, literal.opposite().getSign()));
		} else {
			decomp = new DecompositionSDD(getVTree(), new Element(true, sdd.getLiteral()));
		}
		return apply(decomp, op);
	}

	@Override
	public SDD apply(final DecompositionSDD sdd, final BooleanOperator op) {
		if (false) {
			// TODO: check if it is in cache
			return null;
		} else {
			final List<Element> elements = new ArrayList<Element>();
			final Map<SDD, Element> subs = new HashMap<SDD, Element>();
			for (final Element e1 : expansion()) {
				for (final Element e2 : sdd.expansion()) {
					SDD prime = e1.getPrime().and(e2.getPrime());
					if (prime instanceof DecompositionSDD) {
						((DecompositionSDD) prime).setVTree((InternalNode) sdd.getVTree().getLeft());
					}
					if (prime.isConsistent()) {
						final SDD sub = e1.getSub().apply(e2.getSub(), op);
						if (sub instanceof DecompositionSDD) {
							((DecompositionSDD) sub).setVTree((InternalNode) sdd.getVTree().getRight());
						}
						// Apply compression
						if (subs.containsKey(sub)) {
							final Element elem = subs.get(sub);
							elements.remove(elem);
							prime = elem.getPrime().or(prime);
						}
						final Element element = new Element(prime, sub);
						elements.add(element);
						subs.put(sub, element);
					}
				}
			}
			final Element[] elems = new Element[elements.size()];
			elements.toArray(elems);
			return new DecompositionSDD(sdd.getVTree(), elems);
		}
	}

	@Override
	public SDD trimmed() {
		final List<Element> elements = new ArrayList<Element>();
		for (final Element element : getElements()) {
			elements.add(element.trimmed());
		}
		if (elements.size() == 1 && elements.get(0).getPrime().equals(new ConstantSDD(true))) {
			return elements.get(0).getSub().trimmed();
		} else if (elements.size() == 2 && elements.get(0).getSub().equals(new ConstantSDD(true)) && elements.get(1).getSub().equals(new ConstantSDD(false))) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elements == null) ? 0 : elements.hashCode());
		result = prime * result + ((vtree == null) ? 0 : vtree.hashCode());
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
		final DecompositionSDD other = (DecompositionSDD) obj;
		if (elements == null) {
			if (other.elements != null) {
				return false;
			}
		} else {
			final boolean sameSize = other.elements != null && elements.size() != other.elements.size();
			if (!sameSize) {
				return false;
			}
			final boolean sameSet = new HashSet<Element>(elements).equals(new HashSet<Element>(other.elements));
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

}
