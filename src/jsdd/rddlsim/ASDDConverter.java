package jsdd.rddlsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jsdd.AndOperator;
import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.LiteralSDD;
import jsdd.OperatorApplication;
import jsdd.SDD;
import jsdd.Variable;
import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.vtree.AVTree;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.VTree;
import jsdd.vtree.VariableLeaf;
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
		return dissect(avtree, nodeId, fringe, assignments);
	}

	public ASDD<Double> dissect(final InternalAVTree avtree, final int nodeId, final Set<Integer> fringe, final Map<Integer, Boolean> assignments) {
		final ADDNode node = context.getNode(nodeId);
		if (node instanceof ADDINode) {
			final ADDINode inode = (ADDINode) node;
			final int varId = inode._nTestVarID;
			if (fringe.contains(varId)) {
				final Map<Integer, Boolean> assignmentsCopy = new HashMap<Integer, Boolean>(assignments);
				assignments.put(varId, false);
				assignmentsCopy.put(varId, true);
				dissect(avtree, inode._nLow, fringe, assignments);
				dissect(avtree, inode._nHigh, fringe, assignmentsCopy);
			} else {
				final SDD sdd = createPartition(avtree.getLeft(), assignments);
				final ASDD<Double> asdd = dissect((InternalAVTree) avtree.getRight(), nodeId);
				new AlgebraicElement<Double>(sdd, asdd);
			}
		} else if (node instanceof ADDDNode) {
			final ADDDNode inode = (ADDDNode) node;
			return new AlgebraicTerminal<Double>(inode._dLower);
		}
		return null;
	}

	private SDD createPartition(final VTree vtree, final Map<Integer, Boolean> assignments) {
		if (vtree instanceof VariableLeaf) {
			final int index = ((VariableLeaf) vtree).getVariable().getIndex(); 
			return new LiteralSDD(index, assignments.get(index));
		} else {
			SDD sdd = new ConstantSDD(true);
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

}
