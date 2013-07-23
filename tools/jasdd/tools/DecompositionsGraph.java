package jasdd.tools;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.util.Pair;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Generates the graph of all of a formula's decompositions and their sizes by
 * swapping and rotating vtrees.
 *
 * @author Ricardo Herrmann
 */
public class DecompositionsGraph {

	enum Operation { RL, RR, SW };

	private static final Map<VTree, Integer> sizes = new HashMap<VTree, Integer>();
	private static final Map<VTree, List<Pair<InternalVTree, Operation>>> graph = new HashMap<VTree, List<Pair<InternalVTree, Operation>>>();
	private static final VariableRegistry vars = new VariableRegistry();

	private static PrintWriter outEdges = null;
	private static PrintWriter outNodes = null;
	private static PrintWriter outViz = null;

	public static void main(final String[] args) {
		try {
			outNodes = new PrintWriter("nodes.dat");
			outEdges = new PrintWriter("edges.dat");
			outViz = new PrintWriter("decompgraph.gv");
			explore();
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (outViz != null) {
				outViz.close();
			}
			if (outEdges != null) {
				outEdges.close();
			}
			if (outNodes != null) {
				outNodes.close();
			}
		}
	}

	private static void explore() {
		final DecompositionSDD initial = exampleCnf4();
		final Stack<DecompositionSDD> queue = new Stack<DecompositionSDD>();
		queue.add(initial);
		outViz.println("digraph decompgraph {");
		while (!queue.isEmpty()) {
			final DecompositionSDD sdd = queue.pop();
			final InternalVTree vtree = sdd.getVTree();
			final int size = sdd.size();
			sizes.put(vtree, size);
			outNodes.println(vtree.toString(vars) + " " + size);
			if (vtree.canRotateLeft()) {
				final InternalVTree left = vtree.rotateLeft();
				if (!sizes.containsKey(left)) {
					queue.push((DecompositionSDD) sdd.rotateLeft());
					addEdge(vtree, left, Operation.RL);
				}
			}
			if (vtree.canRotateRight()) {
				final InternalVTree right = vtree.rotateRight();
				if (!sizes.containsKey(right)) {
					queue.push(sdd.rotateRight());
					addEdge(vtree, right, Operation.RR);
				}
			}
			if (!sizes.containsKey(vtree.swap())) {
				queue.push(sdd.swap());
				addEdge(vtree, vtree.swap(), Operation.SW);
			}
		}
		outViz.println("}");
	}

	private static void addEdge(final InternalVTree parent, final InternalVTree child, final Operation oper) {
		outViz.println("\"" + parent.toString(vars) + "\" -> \"" + child.toString(vars) + "\" [label=\"" + oper + "\"]");
		outEdges.println(parent.toString(vars) + " " + child.toString(vars) + " " + oper);
		final Pair<InternalVTree, Operation> pair = Pair.create(child, oper);
		if (graph.containsKey(parent)) {
			final List<Pair<InternalVTree, Operation>> list = graph.get(parent);
			list.add(pair);
			graph.put(parent, list);
		} else {
			final List<Pair<InternalVTree, Operation>> list = new ArrayList<Pair<InternalVTree, Operation>>();
			list.add(pair);
			graph.put(parent, list);
		}
	}

	private static DecompositionSDD exampleCnf4() {
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Variable d = vars.register("D");
		final Variable e = vars.register("E");
		final Variable f = vars.register("F");

		final InternalVTree vtree = new InternalVTree(a, new InternalVTree(b, new InternalVTree(new InternalVTree(c, d), new InternalVTree(e, f))));

		final SDD sddA = DecompositionSDD.buildNormalized(vtree, a);
		final SDD sddNotA = DecompositionSDD.buildNormalized(vtree, a, false);
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, b);
		final SDD sddC = DecompositionSDD.buildNormalized(vtree, c);
		final SDD sddNotC = DecompositionSDD.buildNormalized(vtree, c, false);
		final SDD sddD = DecompositionSDD.buildNormalized(vtree, d);
		final SDD sddNotE = DecompositionSDD.buildNormalized(vtree, e, false);
		final SDD sddF = DecompositionSDD.buildNormalized(vtree, f);

		final SDD sdd1 = sddA.or(sddB).or(sddNotC);
		final SDD sdd2 = sddNotA.or(sddC).or(sddD);
		final SDD sdd3 = sddA.or(sddB).or(sddNotE);
		final SDD sdd4 = sddB.or(sddF);

		final DecompositionSDD sdd = (DecompositionSDD) sdd1.and(sdd2).and(sdd3).and(sdd4);
		return sdd;
	}

}
