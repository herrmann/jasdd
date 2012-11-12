package jsdd.viz;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.InternalNode;
import jsdd.LeafNode;
import jsdd.Literal;
import jsdd.LiteralSDD;
import jsdd.PairedBox;
import jsdd.SDD;
import jsdd.VTree;
import jsdd.Variable;

/**
 * Conversion of SDDs to Graphviz dot format.
 * 
 * @author Ricardo Herrmann
 */
public class GraphvizDumper {

	public static void dump(final DecompositionSDD sdd) {
		System.out.println("digraph sdd {");
		dumpDecomposition(sdd);
		System.out.println("}");
	}

	private static void dumpDecomposition(final DecompositionSDD sdd) {
		dumpDecomposition(sdd, 0, new HashMap<PairedBox, Integer>());
	}

	private static int dumpDecomposition(final DecompositionSDD sdd, final int nextId, final Map<PairedBox, Integer> visited) {
		final int decompId = nextId;
		int id = nextId + 1;
		for (final PairedBox element : sdd.getElements()) {
			id = dumpPairedBox(element, id, visited, decompId);
		}
		return id;
	}

	private static int dumpPairedBox(final PairedBox element, final int nextId, final Map<PairedBox, Integer> visited, final int decompId) {
		if (!visited.containsKey(element)) {
			final SDD prime = element.getPrime();
			final SDD sub = element.getSub();
			final String primeLabel = label(prime);
			final String subLabel = label(sub);
			int leftId = nextId;
			if (!prime.isTerminal()) {
				leftId = dumpDecomposition((DecompositionSDD) prime, nextId, visited);
			}
			int rightId = leftId;
			if (!sub.isTerminal()) {
				rightId = dumpDecomposition((DecompositionSDD) sub, leftId, visited);
			}
			final int elementId = rightId + 1;
			visited.put(element, elementId);
			System.out.println("  e" + elementId + " [shape=record,label=\"<f0> " + primeLabel + "|<f1> " + subLabel + "\"]");
			if (!prime.isTerminal()) {
				System.out.println("  e" + elementId + ":f0 -> v" + nextId);
			}
			if (!sub.isTerminal()) {
				System.out.println("  e" + elementId + ":f1 -> v" + nextId);
			}
			System.out.println("  v" + decompId + " -> e" + elementId);
			return elementId;
		} else {
			final int elementId = visited.get(element);
			System.out.println("  v" + decompId + " -> e" + elementId);
			return nextId;
		}
	}

	private static String label(final SDD sdd) {
		if (sdd.isTerminal()) {
			if (sdd instanceof ConstantSDD) {
				return sdd.toString();
			} else {
				final Literal literal = ((LiteralSDD) sdd).getLiteral();
				return (literal.getSign() ? "" : "-") + letter(literal.getVariable());
			}
		} else {
			return "o";
		}
	}

	private static void dumpEdges(final DecompositionSDD sdd) {
		// TODO Auto-generated method stub
	}

	public static void dump(final VTree vtree) {
		System.out.println("graph vtree {");
		System.out.println("  node [shape=none]");
		dumpVTreeNode(vtree);
		System.out.println("}");
	}

	private static void dumpVTreeNode(final VTree vtree) {
		dumpVTreeNode(vtree, 0);
	}

	private static int dumpVTreeNode(final VTree vtree, final int nextId) {
		if (!vtree.isLeaf()) {
			final InternalNode node = (InternalNode) vtree;
			final int leftId = dumpVTreeNode(node.getLeft(), nextId);
			final int rightId = dumpVTreeNode(node.getRight(), leftId + 1);
			final int parentId = rightId + 1;
			dumpEdge(node.getLeft(), parentId, leftId);
			dumpEdge(node.getRight(), parentId, rightId);
			return parentId;
		} else {
			return nextId;
		}
	}

	private static void dumpEdge(final VTree node, final int parentId, final int nodeId) {
		if (node.isLeaf()) {
			final String letter = letter(((LeafNode) node).getVariable());
			System.out.println("  " + parentId + " -- " + letter + " [headlabel=" + nodeId + "]");
		} else {
			System.out.println("  " + parentId + " -- " + nodeId);
		}
	}

	private static String letter(final Variable variable) {
		return "" + (char) ('A' + variable.getIndex() - 1);
	}

}
