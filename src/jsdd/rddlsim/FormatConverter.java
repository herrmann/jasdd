package jsdd.rddlsim;

import java.util.Iterator;
import java.util.List;

import jsdd.Variable;
import jsdd.VariableRegistry;
import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;
import jsdd.vtree.AVTree;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.ValueLeaf;
import jsdd.vtree.VariableLeaf;
import dd.discrete.ADD;
import dd.discrete.ADDDNode;
import dd.discrete.ADDINode;
import dd.discrete.ADDNode;

/**
 * Utilities for converting ADDs to ASDDs.
 * 
 * @author Ricardo Herrmann
 */
public class FormatConverter {

	private boolean tightVTree = false;

	private ADD context;

	public FormatConverter(final ADD context) {
		this.context = context;
	}

	public ASDD<Double> addToAsdd(final VariableRegistry vars, final int nodeId) {
		return addToAsdd(vars, context.getNode(nodeId));
	}

	public ASDD<Double> addToAsdd(final VariableRegistry vars, final ADDNode add) {
		final InternalAVTree tree = (InternalAVTree) buildRightLinear(vars);
		return addToAsdd(vars, tree, add);
	}

	public AVTree buildRightLinear(final VariableRegistry vars) {
		return buildRightLinear(vars, 0);
	}

	private AVTree buildRightLinear(final VariableRegistry vars, final int index) {
		if (index >= context._alOrder.size()) {
			return new ValueLeaf();
		} else {
			final int varId = (Integer) context._alOrder.get(index);
			final String varName = (String) context._hmID2VarName.get(varId);
			final Variable var = vars.register(varName);
			return new InternalAVTree(var, buildRightLinear(vars, index + 1));
		}
	}

	public ASDD<Double> addToAsdd(final VariableRegistry vars, final AVTree tree, final int nodeId) {
		return addToAsdd(vars, tree, context.getNode(nodeId));
	}

	private ASDD<Double> addToAsdd(final VariableRegistry vars, AVTree tree, final ADDNode add) {
		if (add instanceof ADDDNode) {
			final ADDDNode dnode = ((ADDDNode) add);
			return new AlgebraicTerminal<Double>(dnode._dLower);
		} else if (add instanceof ADDINode) {
			final ADDINode inode = ((ADDINode) add);
			final String varName = varNameInAdd(inode._nTestVarID);
			final Variable var = vars.register(varName);

			if (tightVTree) {
				// Find subtree that is rooted at the current variable
				while (!var.equals(((VariableLeaf) ((InternalAVTree) tree).getLeft()).getVariable())) {
					tree = ((InternalAVTree) tree).getRight();
				}
			}

			final AVTree subtree = ((InternalAVTree) tree).getRight();
			final ADDNode low = context.getNode(inode._nLow);
			final ADDNode high = context.getNode(inode._nHigh);
			final ASDD<Double> left = addToAsdd(vars, subtree, high);
			final ASDD<Double> right = addToAsdd(vars, subtree, low);
			final AlgebraicElement<Double>[] elems = AlgebraicElement.shannon(var, left, right);
			return new DecompositionASDD<Double>((InternalAVTree) tree, elems);
		}
		return null;
	}

	private String varNameInAdd(final int id) {
		return (String) context._hmID2VarName.get(id);
	}

	@SuppressWarnings("unchecked")
	public AVTree buildPairwiseRightLinear() {
		return buildPairwiseRightLinear(context._alOrder);
	}

	@SuppressWarnings("unchecked")
	public AVTree buildPairwiseRightLinear(final VariableRegistry vars) {
		return buildPairwiseRightLinear(vars, context._alOrder);
	}

	public AVTree buildPairwiseRightLinear(final List<Integer> order) {
		return buildPairwiseRightLinear(new VariableRegistry(), order.iterator());
	}

	public AVTree buildPairwiseRightLinear(final VariableRegistry vars, final List<Integer> order) {
		return buildPairwiseRightLinear(vars, order.iterator());
	}

	private AVTree buildPairwiseRightLinear(final VariableRegistry vars, final Iterator<Integer> iter) {
		if (iter.hasNext()) {
			final Integer fst = iter.next();
			final Variable fstVar = vars.register(varNameInAdd(fst));
			if (iter.hasNext()) {
				final Integer snd = iter.next();
				final Variable sndVar = vars.register(varNameInAdd(snd));
				return new InternalAVTree(new InternalVTree(fstVar, sndVar), buildPairwiseRightLinear(vars, iter));
			} else {
				return new InternalAVTree(fstVar);
			}
		} else {
			return new ValueLeaf();
		}
	}

}
