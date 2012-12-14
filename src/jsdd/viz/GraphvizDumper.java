package jsdd.viz;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.InternalNode;
import jsdd.LeafNode;
import jsdd.Literal;
import jsdd.LiteralSDD;
import jsdd.Element;
import jsdd.SDD;
import jsdd.VTree;
import jsdd.Variable;
import jsdd.VariableRegistry;

/**
 * Conversion of SDDs to Graphviz dot format.
 * 
 * @author Ricardo Herrmann
 */
public class GraphvizDumper {

	public static PrintStream out = System.out;

	public static void setOutput(final PrintStream output) {
		out = output;
	}

	public static void dump(final DecompositionSDD sdd, final VariableRegistry vars, final String fileName) throws FileNotFoundException {
		GraphvizDumper.setOutput(new PrintStream(fileName));
		dump(sdd, vars);
	}

	public static void dump(final DecompositionSDD sdd, final VariableRegistry vars) {
		out.println("digraph sdd {");
		out.println("  graph [ordering=\"out\"]");
		final Map<VTree, Integer> vtreeMap = new HashMap<VTree, Integer>();
		dumpVTreeNode(sdd.getVTree(), vtreeMap, vars);
		dumpDecomposition(sdd, vtreeMap, vars);
		out.println("}");
	}

	private static void dumpDecomposition(final DecompositionSDD sdd, final Map<VTree, Integer> vtreeMap, final VariableRegistry vars) {
		dumpDecomposition(sdd, 0, new HashMap<Element, Integer>(), new HashMap<DecompositionSDD, Integer>(), vtreeMap, vars);
	}

	private static int dumpDecomposition(final DecompositionSDD sdd, final int nextId, final Map<Element, Integer> elemCache, final Map<DecompositionSDD, Integer> decompCache, final Map<VTree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!decompCache.containsKey(sdd)) {
			final int decompId = nextId;
			decompCache.put(sdd, decompId);
			out.println("  d" + decompId + " [shape=circle,label=\"" + vtreeMap.get(sdd.getVTree()) + "\"]");
			final List<Integer> ids = new ArrayList<Integer>();
			int id = nextId + 1;
			for (final Element element : sdd.getElements()) {
				id = dumpPairedBox(element, id, elemCache, decompCache, decompId, vtreeMap, vars);
				ids.add(elemCache.get(element));
			}
			out.print("  { rank=same;");
			for (final int eid : ids) {
				out.print(" e" + eid + ";");
			}
			out.println(" }");
			return id;
		} else {
			return nextId;
		}
	}

	private static int dumpPairedBox(final Element element, final int nextId, final Map<Element, Integer> elemCache, final Map<DecompositionSDD, Integer> decompCache, final int decompId, final Map<VTree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!elemCache.containsKey(element)) {
			final SDD prime = element.getPrime();
			final SDD sub = element.getSub();
			final String primeLabel = label(prime, vars);
			final String subLabel = label(sub, vars);
			int leftId = nextId;
			if (!prime.isTerminal()) {
				leftId = dumpDecomposition((DecompositionSDD) prime, nextId, elemCache, decompCache, vtreeMap, vars);
			}
			int rightId = leftId;
			if (!sub.isTerminal()) {
				rightId = dumpDecomposition((DecompositionSDD) sub, leftId, elemCache, decompCache, vtreeMap, vars);
			}
			final int elementId = rightId + 1;
			elemCache.put(element, elementId);
			out.println("  e" + elementId + " [shape=record,label=\"<f0> " + primeLabel + "|<f1> " + subLabel + "\"]");
			if (!prime.isTerminal()) {
				out.println("  e" + elementId + ":f0 -> d" + decompCache.get(prime));
			}
			if (!sub.isTerminal()) {
				out.println("  e" + elementId + ":f1 -> d" + decompCache.get(sub));
			}
			out.println("  d" + decompId + " -> e" + elementId);
			return elementId;
		} else {
			final int elementId = elemCache.get(element);
			out.println("  d" + decompId + " -> e" + elementId);
			return nextId;
		}
	}

	private static String label(final SDD sdd, final VariableRegistry vars) {
		if (sdd.isTerminal()) {
			if (sdd instanceof ConstantSDD) {
				return sdd.toString();
			} else {
				final Literal literal = ((LiteralSDD) sdd).getLiteral();
				final Variable var = literal.getVariable();
				return (literal.getSign() ? "" : "~") + (vars.exists(var) ? vars.name(var) : letter(literal.getVariable()));
			}
		} else {
			return "o";
		}
	}

	public static void dump(final VTree vtree, final VariableRegistry vars) {
		out.println("digraph vtree {");
		dumpVTreeNode(vtree, vars);
		out.println("}");
	}

	public static void dump(final VTree vtree) {
		out.println("digraph vtree {");
		dumpVTreeNode(vtree, new VariableRegistry());
		out.println("}");
	}

	private static void dumpVTreeNode(final VTree vtree, final VariableRegistry vars) {
		dumpVTreeNode(vtree, 0, null, vars);
	}

	private static void dumpVTreeNode(final VTree vtree, final Map<VTree, Integer> vtreeMap, final VariableRegistry vars) {
		final int nodeId = dumpVTreeNode(vtree, 0, vtreeMap, vars);
		if (vtreeMap != null) {
			vtreeMap.put(vtree, nodeId);
		}
	}

	private static int dumpVTreeNode(final VTree vtree, final int nextId, final Map<VTree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!vtree.isLeaf()) {
			final InternalNode node = (InternalNode) vtree;
			final int leftId = dumpVTreeNode(node.getLeft(), nextId, vtreeMap, vars);
			final int rightId = dumpVTreeNode(node.getRight(), leftId + 1, vtreeMap, vars);
			final int parentId = rightId + 1;
			out.println("  v" + parentId + " [shape=none,label=\"" + parentId + "\"]");
			dumpEdge(node.getLeft(), parentId, leftId, vtreeMap, vars);
			dumpEdge(node.getRight(), parentId, rightId, vtreeMap, vars);
			final String leftName = nodeName(node.getLeft(), leftId, vars);
			final String rightName = nodeName(node.getRight(), rightId, vars);
			out.println("  v" + leftName + "_" + rightName + " [label=\"\",width=.1,style=invis]");
			out.println("  v" + leftName + " -> v" + leftName + "_" + rightName + " [style=invis]");
			out.println("  {rank=same v" + leftName + " -> v" + leftName + "_" + rightName + " -> v" + rightName + " [style=invis]}");
			return parentId;
		} else {
			return nextId;
		}
	}

	private static void dumpEdge(final VTree node, final int parentId, final int nodeId, final Map<VTree, Integer> vtreeMap, final VariableRegistry vars) {
		if (vtreeMap != null) {
			vtreeMap.put(node, nodeId);
		}
		if (node.isLeaf()) {
			final String name = nodeName(node, nodeId, vars);
			out.println("  v" + name + " [shape=none,label=\"" + name + "\"]");
			out.println("  v" + parentId + " -> v" + name + " [arrowhead=none,headlabel=" + nodeId + "]");
		} else {
			out.println("  v" + parentId + " -> v" + nodeId + " [arrowhead=none]");
		}
	}

	private static String nodeName(final VTree node, final int nodeId, final VariableRegistry vars) {
		if (node.isLeaf()) {
			final Variable var = ((LeafNode) node).getVariable();
			if (vars.exists(var)) {
				return vars.name(var);
			} else {
				return letter(var);
			}
		} else {
			return Integer.valueOf(nodeId).toString();
		}
	}

	private static String letter(final Variable variable) {
		return "" + (char) ('A' + variable.getIndex() - 1);
	}

	public static void dump(final VTree vtree, final VariableRegistry vars, final String fileName) throws FileNotFoundException {
		GraphvizDumper.setOutput(new PrintStream(fileName));
		dump(vtree, vars);
	}

	public static void dump(final DecompositionSDD sdd) {
		dump(sdd, new VariableRegistry());
	}

	public static void dump(final DecompositionSDD sdd, final String fileName) throws FileNotFoundException {
		dump(sdd, new VariableRegistry(), fileName);
	}

}
