package jsdd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * SDD for a (X,Y)-decomposition of a boolean function.
 * 
 * @author Ricardo Herrmann
 */
public class DecompositionSDD extends AbstractSDD {

	private VTree node;
	private List<PairedBox> elements = new ArrayList<PairedBox>();

	public DecompositionSDD(final VTree node, final PairedBox... elements) {
		this.node = node;
		this.elements.addAll(Arrays.asList(elements));
	}

	public VTree getNode() {
		return node;
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
	public SDD and(SDD sdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unused")
	public SDD apply(final SDD sdd, final BooleanOperator op) {
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
			return new DecompositionSDD(node, (PairedBox[]) elements.toArray());
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
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DecompositionSDD other = (DecompositionSDD) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if ((other.elements != null && elements.size() != other.elements
				.size())
				|| !new HashSet<PairedBox>(elements)
						.equals(new HashSet<PairedBox>(other.elements)))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

}
