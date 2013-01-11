package jasdd.rddlsim;

import jasdd.algebraic.ASDD;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.AndOperator;
import jasdd.bool.ConstantSDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.LiteralSDD;
import jasdd.bool.OperatorApplication;
import jasdd.bool.SDD;
import jasdd.logic.Variable;
import jasdd.vtree.AVTree;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dd.discrete.ADD;
import dd.discrete.ADDDNode;
import dd.discrete.ADDINode;
import dd.discrete.ADDNode;

/**
 * Utility methods for converting rddlsim ADDs to ASDDs.
 * 
 * @author Ricardo Herrmann
 */
public class ASDDConverter {

	private ADD context;

	public ASDDConverter() {
	}

	public ASDDConverter(final ADD context) {
		this.context = context;
	}

	public ASDD<Double> convert(final AVTree vtree) {
		return convert(vtree, new EnumerationPrinter());
	}

	public ASDD<Double> convert(final AVTree vtree, final VariableAssignment callback) {
		final List<Variable> vars = new ArrayList<Variable>(vtree.partitionVariables());
		enumerate(callback, new HashMap<Variable, Boolean>(), vars, 0);
		return null;
	}

	public interface VariableAssignment {
		void assignment(final Map<Variable, Boolean> values);
	}

	public class EnumerationPrinter implements VariableAssignment {
		@Override
		public void assignment(final Map<Variable, Boolean> values) {
			boolean first = true;
			for (final Entry<Variable, Boolean> entry : values.entrySet()) {
				if (!first) {
					System.out.print(",");
				}
				first = false;
				System.out.print(entry.getKey() + "=" + entry.getValue());
			}
			System.out.println();
		}
	}

	private void enumerate(final VariableAssignment callback, final Map<Variable, Boolean> values, final List<Variable> vars, final int index) {
		if (index < vars.size()) {
			final Variable var = vars.get(index);
			final HashMap<Variable, Boolean> copy = new HashMap<Variable, Boolean>(values);
			values.put(var, true);
			copy.put(var, false);
			enumerate(callback, values, vars, index + 1);
			enumerate(callback, copy, vars, index + 1);
		} else {
			callback.assignment(values);
		}
	}

	public ASDD<Double> dissect(final InternalAVTree avtree, final int nodeId) {
		final Set<Integer> fringe = new HashSet<Integer>();
		for (final Variable var : avtree.getLeft().variables()) {
			fringe.add(var.getIndex());
		}
		final Map<Integer, Boolean> assignments = new HashMap<Integer, Boolean>();
		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> asdd = new DecompositionASDD<Double>(avtree);
		dissect(asdd, avtree, nodeId, fringe, assignments);
		return cachedAsdd(asdd);
	}

	public void dissect(final DecompositionASDD<Double> asdd, final InternalAVTree avtree, final int nodeId, final Set<Integer> fringe, final Map<Integer, Boolean> assignments) {
		final ADDNode node = context.getNode(nodeId);
		if (node instanceof ADDINode) {
			final ADDINode inode = (ADDINode) node;
			final int varId = inode._nTestVarID;
			if (fringe.contains(varId)) {
				final Map<Integer, Boolean> assignmentsCopy = new HashMap<Integer, Boolean>(assignments);
				assignments.put(varId, false);
				assignmentsCopy.put(varId, true);
				dissect(asdd, avtree, inode._nLow, fringe, assignments);
				dissect(asdd, avtree, inode._nHigh, fringe, assignmentsCopy);
			} else {
				final SDD prime = createPartition(avtree.getLeft(), assignments);
				final ASDD<Double> sub = dissect((AVTree) avtree.getRight(), nodeId);
				final AlgebraicElement<Double> elem = new AlgebraicElement<Double>(prime, sub);
				asdd.addElement(cachedAlgebraicElement(elem));
			}
		} else if (node instanceof ADDDNode) {
			final ADDDNode inode = (ADDDNode) node;
			final SDD prime = createPartition(avtree.getLeft(), assignments);
			final AlgebraicTerminal<Double> sub = cachedAsdd(new AlgebraicTerminal<Double>(inode._dLower));
			final AlgebraicElement<Double> elem = new AlgebraicElement<Double>(prime, sub);
			asdd.addElement(cachedAlgebraicElement(elem));
		}
	}

	private SDD createPartition(final VTree vtree, final Map<Integer, Boolean> assignments) {
		if (vtree instanceof VariableLeaf) {
			final int index = ((VariableLeaf) vtree).getVariable().getIndex(); 
			return cachedSdd(new LiteralSDD(index, assignments.get(index)));
		} else {
			SDD sdd = cachedSdd(new ConstantSDD(true));
			for (final Entry<Integer, Boolean> entry : assignments.entrySet()) {
				final int index = entry.getKey();
				final boolean sign = entry.getValue();
				final Variable var = new Variable(index);
				final SDD term = DecompositionSDD.buildNormalized((InternalVTree) vtree, var, sign);
				sdd = cachedSdd(new OperatorApplication(sdd, term, new AndOperator()).apply());
			}
			return sdd;
		}
	}

	public ASDD<Double> dissect(final AVTree avtree, final int nodeId) {
		final ADDNode node = context.getNode(nodeId);
		if (node instanceof ADDDNode) {
			if (avtree.isLeaf()) {
				final ADDDNode dnode = (ADDDNode) node;
				final double value = dnode._dLower;
				final AlgebraicTerminal<Double> asdd = cachedAsdd(new AlgebraicTerminal<Double>(value));
				return asdd;
			} else {
				final SDD prime = new ConstantSDD(true);
				final AVTree rightTree = ((InternalAVTree) avtree).getRight();
				final ASDD<Double> sub = dissect(rightTree, nodeId);
				final AlgebraicElement<Double> elem = cachedAlgebraicElement(new AlgebraicElement<Double>(prime, sub));
				@SuppressWarnings("unchecked")
				final ASDD<Double> asdd = new DecompositionASDD<Double>((InternalAVTree) avtree, elem);
				return cachedAsdd(asdd);
			}
		} else if (node instanceof ADDINode) {
			if (avtree.isLeaf()) {
				throw new RuntimeException("This shouldn't happen");
			} else {
				final InternalAVTree tree = (InternalAVTree) avtree;
				return dissect(tree, nodeId);
			}
		}
		throw new RuntimeException("This shouldn't happen");
	}

	private Map<AlgebraicElement<Double>, AlgebraicElement<Double>> algebraicElementCache = new HashMap<AlgebraicElement<Double>, AlgebraicElement<Double>>();

	private Map<ASDD<Double>, ASDD<Double>> algebraicCache = new HashMap<ASDD<Double>, ASDD<Double>>();
	
	private Map<SDD, SDD> cache = new HashMap<SDD, SDD>();

	private AlgebraicElement<Double> cachedAlgebraicElement(final AlgebraicElement<Double> element) {
		final AlgebraicElement<Double> cached = algebraicElementCache.get(element);
		if (null == cached) {
			algebraicElementCache.put(element, element);
			return element;
		} else {
			// System.out.println("Element cache hit: " + element);
			return cached;
		}
	}

	private <T extends ASDD<Double>> T cachedAsdd(final T asdd) {
		@SuppressWarnings("unchecked")
		final T cached = (T) algebraicCache.get(asdd);
		if (null == cached) {
			algebraicCache.put(asdd, asdd);
			return asdd;
		} else {
			// System.out.println("Element cache hit: " + asdd);
			return cached;
		}
	}

	private <T extends SDD> T cachedSdd(final T sdd) {
		@SuppressWarnings("unchecked")
		final T cached = (T) cache.get(sdd);
		if (null == cached) {
			cache.put(sdd, sdd);
			return sdd;
		} else {
			// System.out.println("Element cache hit: " + sdd);
			return cached;
		}
	}

}
