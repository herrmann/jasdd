package jsdd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DecompositionSDD extends SDD {

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

}
