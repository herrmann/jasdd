package jsdd.viz;

import java.util.HashMap;
import java.util.Map;

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
		final Map<VTree, Integer> vtreeMap = new HashMap<VTree, Integer>();
		dumpVTreeNode(sdd.getVTree(), vtreeMap);
		dumpDecomposition(sdd, vtreeMap);
		System.out.println("}");
	}

	private static void dumpDecomposition(final DecompositionSDD sdd, final Map<VTree, Integer> vtreeMap) {
		dumpDecomposition(sdd, 0, new HashMap<PairedBox, Integer>(), vtreeMap);
	}

	private static int dumpDecomposition(final DecompositionSDD sdd, final int nextId, final Map<PairedBox, Integer> visited, final Map<VTree, Integer> vtreeMap) {
		final int decompId = nextId;
		System.out.println("  d" + decompId + " [label=\"" + vtreeMap.get(sdd.getVTree()) + "\"]");
		int id = nextId + 1;
		for (final PairedBox element : sdd.getElements()) {
			id = dumpPairedBox(element, id, visited, decompId, vtreeMap);
		}
		return id;
	}

	private static int dumpPairedBox(final PairedBox element, final int nextId, final Map<PairedBox, Integer> visited, final int decompId, final Map<VTree, Integer> vtreeMap) {
		if (!visited.containsKey(element)) {
			final SDD prime = element.getPrime();
			final SDD sub = element.getSub();
			final String primeLabel = label(prime);
			final String subLabel = label(sub);
			int leftId = nextId;
			if (!prime.isTerminal()) {
				leftId = dumpDecomposition((DecompositionSDD) prime, nextId, visited, vtreeMap);
			}
			int rightId = leftId;
			if (!sub.isTerminal()) {
				rightId = dumpDecomposition((DecompositionSDD) sub, leftId, visited, vtreeMap);
			}
			final int elementId = rightId + 1;
			visited.put(element, elementId);
			System.out.println("  e" + elementId + " [shape=record,label=\"<f0> " + primeLabel + "|<f1> " + subLabel + "\"]");
			if (!prime.isTerminal()) {
				System.out.println("  e" + elementId + ":f0 -> d" + nextId);
			}
			if (!sub.isTerminal()) {
				System.out.println("  e" + elementId + ":f1 -> d" + nextId);
			}
			System.out.println("  d" + decompId + " -> e" + elementId);
			return elementId;
		} else {
			final int elementId = visited.get(element);
			System.out.println("  d" + decompId + " -> e" + elementId);
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

	public static void dump(final VTree vtree) {
		System.out.println("digraph vtree {");
		dumpVTreeNode(vtree);
		System.out.println("}");
	}

	private static void dumpVTreeNode(final VTree vtree) {
		dumpVTreeNode(vtree, 0, null);
	}

	private static void dumpVTreeNode(final VTree vtree, final Map<VTree, Integer> vtreeMap) {
		final int nodeId = dumpVTreeNode(vtree, 0, vtreeMap);
		if (vtreeMap != null) {
			vtreeMap.put(vtree, nodeId);
		}
	}

	private static int dumpVTreeNode(final VTree vtree, final int nextId, final Map<VTree, Integer> vtreeMap) {
		if (!vtree.isLeaf()) {
			final InternalNode node = (InternalNode) vtree;
			final int leftId = dumpVTreeNode(node.getLeft(), nextId, vtreeMap);
			final int rightId = dumpVTreeNode(node.getRight(), leftId + 1, vtreeMap);
			final int parentId = rightId + 1;
			System.out.println("  v" + parentId + " [shape=none,label=\"" + parentId + "\"]");
			dumpEdge(node.getLeft(), parentId, leftId, vtreeMap);
			dumpEdge(node.getRight(), parentId, rightId, vtreeMap);
			return parentId;
		} else {
			return nextId;
		}
	}

	private static void dumpEdge(final VTree node, final int parentId, final int nodeId, final Map<VTree, Integer> vtreeMap) {
		if (vtreeMap != null) {
			vtreeMap.put(node, nodeId);
		}
		if (node.isLeaf()) {
			final String letter = letter(((LeafNode) node).getVariable());
			System.out.println("  v" + letter + " [shape=none,label=\"" + letter + "\"]");
			System.out.println("  v" + parentId + " -> v" + letter + " [arrowhead=none,headlabel=" + nodeId + "]");
		} else {
			System.out.println("  v" + parentId + " -> v" + nodeId + " [arrowhead=none]");
		}
	}

	private static String letter(final Variable variable) {
		return "" + (char) ('A' + variable.getIndex() - 1);
	}

}
