package jsdd.rddlsim;

import java.util.Iterator;
import java.util.List;

import dd.discrete.ADD;

import jsdd.Variable;
import jsdd.VariableRegistry;
import jsdd.vtree.AVTree;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.ValueLeaf;

public class PairwiseAVTreeConverter {

	private ADD context;

	public PairwiseAVTreeConverter(final ADD context) {
		this.context = context;
	}

	private String varNameInAdd(final int id) {
		return (String) context._hmID2VarName.get(id);
	}

	@SuppressWarnings("unchecked")
	public AVTree build() {
		return build(context._alOrder);
	}

	@SuppressWarnings("unchecked")
	public AVTree build(final VariableRegistry vars) {
		return build(vars, context._alOrder);
	}

	public AVTree build(final List<Integer> order) {
		return build(new VariableRegistry(), order.iterator());
	}

	public AVTree build(final VariableRegistry vars, final List<Integer> order) {
		return build(vars, order.iterator());
	}

	private AVTree build(final VariableRegistry vars, final Iterator<Integer> iter) {
		if (iter.hasNext()) {
			final Integer fst = iter.next();
			final Variable fstVar = vars.register(varNameInAdd(fst));
			if (iter.hasNext()) {
				final Integer snd = iter.next();
				final Variable sndVar = vars.register(varNameInAdd(snd));
				return new InternalAVTree(new InternalVTree(fstVar, sndVar), build(vars, iter));
			} else {
				return new InternalAVTree(fstVar);
			}
		} else {
			return new ValueLeaf();
		}
	}

}
