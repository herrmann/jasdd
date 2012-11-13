package jsdd;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import jsdd.viz.GraphvizDumper;

/**
 * SDD for a (X,Y)-decomposition of a boolean function.
 * 
 * @author Ricardo Herrmann
 */
public class DecompositionSDD extends AbstractSDD {

	private VTree vtree;
	private List<PairedBox> elements = new ArrayList<PairedBox>();

	public DecompositionSDD(final DecompositionSDD sdd) {
		this.vtree = sdd.getVTree();
		this.elements = (List<PairedBox>) sdd.getElements();
		// TODO: Deep copy
	}

	public DecompositionSDD(final VTree node, final PairedBox... elements) {
		this.vtree = node;
		for (final PairedBox element : elements) {
			element.addParent(this);
			this.elements.add(element);
		}
	}

	@Override
	public VTree getVTree() {
		return vtree;
	}

	public Collection<PairedBox> getElements() {
		return elements;
	}

	public boolean isStronglyDeterministic() {
		for (final PairedBox i : elements) {
			for (final PairedBox j : elements) {
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
			for (final PairedBox element : elements) {
				if (!element.isUnsatisfiable()) {
					if (!first) {
						sb.append(" \\/ ");
					}
					first = false;
					sb.append(element.toStringBuilder());
				}
			}
		}
		return sb;
	}

	@Override
	public boolean isTautology() {
		for (final PairedBox element : elements) {
			if (element.isTautology()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isUnsatisfiable() {
		for (final PairedBox element : elements) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SDD apply(final DecompositionSDD sdd, final BooleanOperator op) {
		if (false) {
			// TODO: check if it is in cache
			return null;
		} else {
			final List<PairedBox> elements = new ArrayList<PairedBox>();
			for (final PairedBox e1 : expansion()) {
				for (final PairedBox e2 : sdd.expansion()) {
					final SDD prime = e1.getPrime().and(e2.getPrime());
					if (prime.isConsistent()) {
						final SDD sub = e1.getSub().apply(e2.getSub(), op);
						elements.add(new PairedBox(prime, sub));
					}
				}
			}
			final PairedBox[] elems = new PairedBox[elements.size()];
			elements.toArray(elems);
			return new DecompositionSDD(vtree, elems);
		}
	}

	@Override
	public Collection<PairedBox> expansion() {
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
			final boolean sameSet = new HashSet<PairedBox>(elements).equals(new HashSet<PairedBox>(other.elements));
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
