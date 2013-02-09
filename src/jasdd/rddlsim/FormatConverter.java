package jasdd.rddlsim;

import jasdd.algebraic.ASDD;
import jasdd.algebraic.ASDDFactory;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;
import jasdd.bool.SDDFactory;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.AVTree;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.ValueLeaf;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	public ASDD<Double> pairwise(final VariableRegistry vars, final AVTree tree, final int nodeId) {
		return pairwise(vars, tree, context.getNode(nodeId));
	}

	private ASDD<Double> addToAsdd(final VariableRegistry vars, AVTree tree, final ADDNode add) {
		if (add instanceof ADDDNode) {
			final ADDDNode dnode = ((ADDDNode) add);
			return ASDDFactory.getInstance().createTerminal(dnode._dLower);
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
			return ASDDFactory.getInstance().createDecomposition((InternalAVTree) tree, elems);
		}
		return null;
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
				final SDDFactory factory = SDDFactory.getInstance();
				final DecompositionSDD partitionHighHigh = factory.createDecomposition(subtree, factory.shannon(leftVar, rightVar, false)); // A /\ B
				final DecompositionSDD partitionHighLow = factory.createDecomposition(subtree, factory.shannon(leftVar, rightVar, false, false)); // A /\ ~B
				final DecompositionSDD partitionLowHigh = factory.createDecomposition(subtree, factory.shannon(leftVar, false, rightVar)); // ~A /\ B
				final DecompositionSDD partitionLowLow = factory.createDecomposition(subtree, factory.shannon(leftVar, false, rightVar, false)); // ~A /\ ~B

				final ASDD<Double> subHighHigh = pairwise(vars, ((InternalAVTree) tree).getRight(), highHigh2);
				final ASDD<Double> subHighLow = pairwise(vars, ((InternalAVTree) tree).getRight(), highLow2);
				final ASDD<Double> subLowHigh = pairwise(vars, ((InternalAVTree) tree).getRight(), lowHigh2);
				final ASDD<Double> subLowLow = pairwise(vars, ((InternalAVTree) tree).getRight(), lowLow2);

				final Map<ASDD<Double>, Set<DecompositionSDD>> elements = new HashMap<ASDD<Double>, Set<DecompositionSDD>>();
				cluster(elements, subHighHigh, partitionHighHigh);
				cluster(elements, subHighLow, partitionHighLow);
				cluster(elements, subLowHigh, partitionLowHigh);
				cluster(elements, subLowLow, partitionLowLow);

				return ASDDFactory.getInstance().createDecomposition((InternalAVTree) tree, compress(elements));
			}
		}
		return null;
	}

	private AlgebraicElement<Double>[] compress(final Map<ASDD<Double>, Set<DecompositionSDD>> elements) {
		@SuppressWarnings("unchecked")
		final AlgebraicElement<Double>[] partitions = new AlgebraicElement[elements.size()];
		int i = 0;
		for (final Entry<ASDD<Double>, Set<DecompositionSDD>> elem : elements.entrySet()) {
			if (elem.getValue().size() > 1) {
				final Iterator<DecompositionSDD> iter = elem.getValue().iterator();
				SDD prime = iter.next();
				while (iter.hasNext()) {
					prime = prime.or(iter.next());
				}
				partitions[i++] = ASDDFactory.getInstance().createElement(prime, elem.getKey());
			} else {
				partitions[i++] = ASDDFactory.getInstance().createElement(elem.getValue().iterator().next(), elem.getKey());
			}
		}
		return partitions;
	}

	private void cluster(final Map<ASDD<Double>, Set<DecompositionSDD>> elements, final ASDD<Double> sub, final DecompositionSDD partition) {
		if (elements.containsKey(sub)) {
			final Set<DecompositionSDD> set = elements.get(sub);
			set.add(partition);
		} else {
			final Set<DecompositionSDD> set = new HashSet<DecompositionSDD>();
			set.add(partition);
			elements.put(sub, set);
		}
	}

	private String varNameInAdd(final int id) {
		return (String) context._hmID2VarName.get(id);
	}

	public static final String FILENAME_DEFAULT = "add.dot";

	public void dumpAddAsAsdd(final int nodeId) {
		dumpAddAsAsdd(nodeId, FILENAME_DEFAULT);
	}

	public void dumpAddAsAsdd(final int nodeId, final String fileName) {
		final VariableRegistry vars = new VariableRegistry();
		final AVTree tree = new FormatConverter(context).buildRightLinear(vars);
		final FormatConverter converter = new FormatConverter(context);
		final ASDD<Double> asdd = converter.addToAsdd(vars, tree, nodeId);
		if (asdd instanceof DecompositionASDD<?>) {
			try {
				GraphvizDumper.dump((DecompositionASDD<Double>) asdd, vars, fileName);
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void dumpPairwise(final int nodeId) {
		dumpPairwise(nodeId, FILENAME_DEFAULT);
	}

	public void dumpPairwise(final int nodeId, final String fileName) {
		final VariableRegistry vars = new VariableRegistry();
		final AVTree tree = new PairwiseAVTreeConverter(context).build(vars);
		final FormatConverter converter = new FormatConverter(context);
		final ASDD<Double> asdd = converter.pairwise(vars, tree, nodeId);
		if (asdd instanceof DecompositionASDD<?>) {
			try {
				GraphvizDumper.dump((DecompositionASDD<Double>) asdd, vars, fileName);
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
