package jsdd.rddlsim;

import jsdd.DecompositionSDD;
import jsdd.Element;
import jsdd.Variable;
import jsdd.VariableRegistry;
import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;
import jsdd.vtree.AVTree;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.VTree;
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

	public ASDD<Double> pairwise(final VariableRegistry vars, final AVTree tree, final int nodeId) {
		return pairwise(vars, tree, context.getNode(nodeId));
	}

	public ASDD<Double> pairwise(final VariableRegistry vars, final AVTree tree, final ADDNode add) {
		if (add instanceof ADDDNode) {
			return addToAsdd(vars, tree, add);
		} else if (add instanceof ADDINode) {
			final ADDINode inode = ((ADDINode) add);
			final ADDNode high = context.getNode(inode._nHigh);
			final ADDNode low = context.getNode(inode._nLow);
			if (high instanceof ADDDNode) {
				return addToAsdd(vars, tree, add);
			} else {
				final ADDINode inodeHigh2 = ((ADDINode) high);
				final ADDINode inodeLow2 = ((ADDINode) low);
				final ADDNode highHigh2 = context.getNode(inodeHigh2._nHigh);
				final ADDNode highLow2 = context.getNode(inodeHigh2._nLow);
				final ADDNode lowHigh2 = context.getNode(inodeLow2._nHigh);
				final ADDNode lowLow2 = context.getNode(inodeLow2._nLow);
				final InternalVTree subtree = (InternalVTree) ((InternalAVTree) tree).getLeft();
				final Variable leftVar = ((VariableLeaf) subtree.getLeft()).getVariable();
				final Variable rightVar = ((VariableLeaf) subtree.getRight()).getVariable();
				final DecompositionSDD partitionHighHigh = new DecompositionSDD(subtree, Element.shannon(leftVar, rightVar, false)); // A /\ B
				final DecompositionSDD partitionHighLow = new DecompositionSDD(subtree, Element.shannon(leftVar, rightVar, false, false)); // A /\ ~B
				final DecompositionSDD partitionLowHigh = new DecompositionSDD(subtree, Element.shannon(leftVar, false, rightVar)); // ~A /\ B
				final DecompositionSDD partitionLowLow = new DecompositionSDD(subtree, Element.shannon(leftVar, false, rightVar, false)); // ~A /\ ~B
				final ASDD<Double> subHighHigh = pairwise(vars, ((InternalAVTree) tree).getRight(), highHigh2);
				final ASDD<Double> subHighLow = pairwise(vars, ((InternalAVTree) tree).getRight(), highLow2);
				final ASDD<Double> subLowHigh = pairwise(vars, ((InternalAVTree) tree).getRight(), lowHigh2);
				final ASDD<Double> subLowLow = pairwise(vars, ((InternalAVTree) tree).getRight(), lowLow2);
				return new DecompositionASDD<Double>((InternalAVTree) tree,
					new AlgebraicElement<Double>(partitionHighHigh, subHighHigh),
					new AlgebraicElement<Double>(partitionHighLow, subHighLow),
					new AlgebraicElement<Double>(partitionLowHigh, subLowHigh),
					new AlgebraicElement<Double>(partitionLowLow, subLowLow));
			}
		}
		return null;
	}

	private String varNameInAdd(final int id) {
		return (String) context._hmID2VarName.get(id);
	}

}
