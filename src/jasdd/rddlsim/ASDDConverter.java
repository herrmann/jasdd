package jasdd.rddlsim;

import jasdd.JASDD;
import jasdd.algebraic.ASDD;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.AndOperator;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.OperatorApplication;
import jasdd.bool.SDD;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.AVTree;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
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
		final List<AlgebraicElement<Double>> elems = new ArrayList<AlgebraicElement<Double>>();
		dissect(elems, avtree, nodeId, fringe, assignments);
		return JASDD.createDecomposition(avtree, elems);
	}

	public void dissect(final List<AlgebraicElement<Double>> elems, final InternalAVTree avtree, final int nodeId, final Set<Integer> fringe, final Map<Integer, Boolean> assignments) {
		final ADDNode node = context.getNode(nodeId);
		if (node instanceof ADDINode) {
			final ADDINode inode = (ADDINode) node;
			final int varId = inode._nTestVarID;
			if (fringe.contains(varId)) {
				final Map<Integer, Boolean> assignmentsCopy = new HashMap<Integer, Boolean>(assignments);
				assignments.put(varId, false);
				assignmentsCopy.put(varId, true);
				dissect(elems, avtree, inode._nLow, fringe, assignments);
				dissect(elems, avtree, inode._nHigh, fringe, assignmentsCopy);
			} else {
				final SDD prime = createPartition(avtree.getLeft(), assignments);
				final ASDD<Double> sub = dissect(avtree.getRight(), nodeId);
				elems.add(JASDD.createElement(prime, sub));
			}
		} else if (node instanceof ADDDNode) {
			final ADDDNode inode = (ADDDNode) node;
			final SDD prime = createPartition(avtree.getLeft(), assignments);
			final AlgebraicTerminal<Double> sub = JASDD.createTerminal(inode._dLower);
			elems.add(JASDD.createElement(prime, sub));
		}
	}

	private SDD createPartition(final VTree vtree, final Map<Integer, Boolean> assignments) {
		if (vtree instanceof VariableLeaf) {
			final int index = ((VariableLeaf) vtree).getVariable().getIndex();
			if (assignments.containsKey(index)) {
				return JASDD.createLiteral(index, assignments.get(index));
			} else {
				return JASDD.createTrue();
			}
		} else {
			SDD sdd = JASDD.createTrue();
			for (final Entry<Integer, Boolean> entry : assignments.entrySet()) {
				final int index = entry.getKey();
				final boolean sign = entry.getValue();
				final Variable var = new Variable(index);
				final SDD term = DecompositionSDD.buildNormalized((InternalVTree) vtree, var, sign);
				sdd = new OperatorApplication(sdd, term, new AndOperator()).apply();
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
				final AlgebraicTerminal<Double> asdd = JASDD.createTerminal(value);
				return asdd;
			} else {
				final SDD prime = JASDD.createTrue();
				final AVTree rightTree = ((InternalAVTree) avtree).getRight();
				final ASDD<Double> sub = dissect(rightTree, nodeId);
				final AlgebraicElement<Double> elem = JASDD.createElement(prime, sub);
				return JASDD.createDecomposition((InternalAVTree) avtree, elem);
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

	public ASDD<Double> convert(final int nodeId, final AVTree avtree, /* remove this param */ final VariableRegistry vars) throws FileNotFoundException {
		final ADDNode node = context.getNode(nodeId);
		if (node instanceof ADDDNode) {
			if (avtree.isLeaf()) {
				final ADDDNode dnode = (ADDDNode) node;
				final double value = dnode._dLower;
				final AlgebraicTerminal<Double> asdd = JASDD.createTerminal(value);
				return asdd;
			} else {
				final SDD prime = JASDD.createTrue();
				final AVTree rightTree = ((InternalAVTree) avtree).getRight();
				final ASDD<Double> sub = dissect(rightTree, nodeId);
				final AlgebraicElement<Double> elem = JASDD.createElement(prime, sub);
				return JASDD.createDecomposition((InternalAVTree) avtree, elem);
			}
		} else if (node instanceof ADDINode) {
			final ADDINode inode = (ADDINode) node;
			final int varId = inode._nTestVarID;

			final ASDD<Double> low = convert(inode._nLow, avtree, vars);
			GraphvizDumper.dump((DecompositionASDD<Double>) low, vars, "fn_low.gv");

			final ASDD<Double> high = convert(inode._nHigh, avtree, vars);
			GraphvizDumper.dump((DecompositionASDD<Double>) high, vars, "fn_high.gv");

			final Variable a = vars.register("A");
		    final InternalVTree vtree = new InternalVTree(vars.register("B"), new InternalVTree(a, vars.register("V")));
			final DecompositionSDD lowPartition = JASDD.buildNormalized(vtree, a);
			GraphvizDumper.dump(lowPartition, vars, "fn_high_part.gv");
		}
		return null;
	}

}
