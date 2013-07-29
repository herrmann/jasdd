package jasdd.tools;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.vtree.Direction;
import jasdd.vtree.InternalTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Stack;

/**
 * Generates the graph of all of a formula's decompositions and their sizes by
 * swapping and rotating vtrees.
 *
 * @author Ricardo Herrmann
 */
public class DecompositionsGraph {

	private static final VariableRegistry vars = new VariableRegistry();
	private static final OperationsGraph graph = new OperationsGraph();

	private static PrintWriter outEdges = null;
	private static PrintWriter outNodes = null;
	private static PrintWriter outViz = null;

	public static void main(final String[] args) {
		try {
			outNodes = new PrintWriter("nodes.dat");
			outNodes.println("Nodes Id Label Size");
			outEdges = new PrintWriter("edges.dat");
			outEdges.println("Source Target Type Id Label Weight");
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
		final Stack<SDD> queue = new Stack<SDD>();
		queue.add(initial);
		outViz.println("digraph decompgraph {");
		while (!queue.isEmpty()) {
			final SDD sdd = queue.pop();
			if (sdd instanceof DecompositionSDD) {
				final DecompositionSDD decomp = (DecompositionSDD) sdd;
				final InternalVTree vtree = decomp.getVTree();
				final int size = sdd.size();
				addNode(vtree, size);
				exploreAlternatives(queue, decomp, vtree, new Path());
			}
		}
		outViz.println("}");
	}

	private static void addNode(final InternalVTree vtree, final int size) {
		graph.addNode(vtree, size);
		outNodes.println(graph.nodeId(vtree) + " " + graph.nodeId(vtree) + " " + vtree.toString(vars) + " " + size);
	}

	private static void exploreAlternatives(final Stack<SDD> queue, final DecompositionSDD sdd, final InternalVTree vtree, final Path path) {
		System.out.println(vtree + " " + path);
		if (vtree.canSwap(path.iterator())) {
			final InternalTree<VTree> swapped = vtree.swap(path.cloneableIterator());
			if (!graph.contains(swapped) && sdd.canSwap(path.cloneableIterator())) {
				final SDD newSdd = sdd.swap(path.cloneableIterator());
				addNode((InternalVTree) swapped, newSdd.size());
				queue.push(newSdd);
			}
			addEdge(vtree, (InternalVTree) swapped, Operation.SWAP, path);
			if (vtree.canRotateLeft(path.iterator())) {
				@SuppressWarnings("unchecked")
				final InternalTree<VTree> left = (InternalTree<VTree>) vtree.rotateLeft(path.cloneableIterator());
				if (!graph.contains(left) && sdd.canRotateLeft(path.cloneableIterator())) {
					final SDD newSdd = sdd.rotateLeft(path.cloneableIterator());
					addNode((InternalVTree) left, newSdd.size());
					queue.push(sdd.rotateLeft(path.cloneableIterator()));
				}
				addEdge(vtree, (InternalVTree) left, Operation.ROTATE_LEFT, path);
			}
			if (vtree.canRotateRight(path.iterator())) {
				@SuppressWarnings("unchecked")
				final InternalTree<VTree> right = (InternalTree<VTree>) vtree.rotateRight(path.cloneableIterator());
				if (!graph.contains(right) && sdd.canRotateRight(path.cloneableIterator())) {
					final SDD newSdd = sdd.rotateRight(path.cloneableIterator());
					addNode((InternalVTree) right, newSdd.size());
					queue.push(newSdd);
				}
				addEdge(vtree, (InternalVTree) right, Operation.ROTATE_RIGHT, path);
			}
			final Path clonedPath = path.clone();
			path.add(Direction.LEFT);
			clonedPath.add(Direction.RIGHT);
			exploreAlternatives(queue, sdd, vtree, path);
			exploreAlternatives(queue, sdd, vtree, clonedPath);
		}
	}

	private static int edgeCount = 0;

	private static void addEdge(final InternalVTree parent, final InternalVTree child, final Operation oper, final Path path) {
		final EdgeInfo edgeInfo = new EdgeInfo(oper, path);
		outViz.println("\"" + parent.toString(vars) + "\" -> \"" + child.toString(vars) + "\" [label=\"" + edgeInfo + "\"]");
		outEdges.println(graph.nodeId(parent) + " " + graph.nodeId(child) + " Directed " + (++edgeCount) + " " + edgeInfo + " 1");
		graph.addEdge(parent, child, edgeInfo);
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
