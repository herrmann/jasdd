package jsdd.viz;

import jsdd.DecompositionSDD;
import jsdd.InternalNode;
import jsdd.LeafNode;
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
		dumpNodes(sdd);
		dumpEdges(sdd);
		System.out.println("}");
	}

	private static void dumpNodes(final DecompositionSDD sdd) {
		// TODO Auto-generated method stub
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
