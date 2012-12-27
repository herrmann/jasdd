package jsdd.viz;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.Element;
import jsdd.Literal;
import jsdd.LiteralSDD;
import jsdd.SDD;
import jsdd.Variable;
import jsdd.VariableRegistry;
import jsdd.algebraic.ASDD;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.DecompositionASDD;
import jsdd.vtree.InternalTree;
import jsdd.vtree.Tree;
import jsdd.vtree.VTree;
import jsdd.vtree.VariableLeaf;

/**
 * Conversion of SDDs to Graphviz dot format.
 * 
 * @author Ricardo Herrmann
 */
public class GraphvizDumper {

	private static final String COLOR_ELEM      = "#B7ECFF";
	private static final String COLOR_DECOMP    = "#E8ADA0";
	private static final String COLOR_ELEM_AL   = "#FFF1BD";
	private static final String COLOR_DECOMP_AL = "#A3E89D";
	private static final String COLOR_VAL       = "#DFB0FF";

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
		final Map<Tree, Integer> vtreeMap = new HashMap<Tree, Integer>();
		dumpVTreeNode(sdd.getVTree(), vtreeMap, vars);
		dumpDecomposition(sdd, vtreeMap, vars);
		out.println("}");
	}

	private static void dumpDecomposition(final DecompositionSDD sdd, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		dumpDecomposition(sdd, 0, new HashMap<Element, Integer>(), new HashMap<DecompositionSDD, Integer>(), vtreeMap, vars);
	}

	private static int dumpDecomposition(final DecompositionSDD sdd, final int nextId, final Map<Element, Integer> elemCache, final Map<DecompositionSDD, Integer> decompCache, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!decompCache.containsKey(sdd)) {
			final int decompId = nextId;
			decompCache.put(sdd, decompId);
			out.println("  d" + decompId + " [style=filled,fillcolor=\"" + COLOR_DECOMP + "\",shape=circle,label=\"" + vtreeMap.get(sdd.getVTree()) + "\"]");
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

	private static int dumpPairedBox(final Element element, final int nextId, final Map<Element, Integer> elemCache, final Map<DecompositionSDD, Integer> decompCache, final int decompId, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
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
			out.println("  e" + elementId + " [style=filled,fillcolor=\"" + COLOR_ELEM +"\",shape=record,label=\"<f0> " + primeLabel + "|<f1> " + subLabel + "\"]");
			if (!prime.isTerminal()) {
				out.println("  e" + elementId + ":f0:c -> d" + decompCache.get(prime) + " [tailclip=false]");
			}
			if (!sub.isTerminal()) {
				out.println("  e" + elementId + ":f1:c -> d" + decompCache.get(sub) + " [tailclip=false]");
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
			return "&#9679;";
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

	private static void dumpVTreeNode(final Tree vtree, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		final int nodeId = dumpVTreeNode(vtree, 0, vtreeMap, vars);
		if (vtreeMap != null) {
			vtreeMap.put(vtree, nodeId);
		}
	}

	private static <T extends Tree> int dumpVTreeNode(final Tree vtree, final int nextId, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!vtree.isLeaf()) {
			@SuppressWarnings("unchecked")
			final InternalTree<T> node = (InternalTree<T>) vtree;
			final int leftId = dumpVTreeNode(node.getLeft(), nextId, vtreeMap, vars);
			final int rightId = dumpVTreeNode(node.getRight(), leftId + 1, vtreeMap, vars);
			final int parentId = rightId + 1;
			out.println("  v" + parentId + " [shape=none,label=\"" + parentId + "\"]");
			dumpEdge(node.getLeft(), parentId, leftId, vtreeMap, vars);
			dumpEdge(node.getRight(), parentId, rightId, vtreeMap, vars);
			out.println("  v" + leftId + "_" + rightId + " [label=\"\",width=.1,style=invis]");
			out.println("  v" + leftId + " -> v" + leftId + "_" + rightId + " [style=invis]");
			out.println("  {rank=same v" + leftId + " -> v" + leftId + "_" + rightId + " -> v" + rightId + " [style=invis]}");
			return parentId;
		} else {
			return nextId;
		}
	}

	private static void dumpEdge(final Tree node, final int parentId, final int nodeId, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		if (vtreeMap != null) {
			vtreeMap.put(node, nodeId);
		}
		if (node.isLeaf()) {
			final String name = nodeName(node, nodeId, vars);
			out.println("  v" + nodeId + " [shape=none,label=\"" + name + "\"]");
			out.println("  v" + parentId + " -> v" + nodeId + " [arrowhead=none,headlabel=" + nodeId + "]");
		} else {
			out.println("  v" + parentId + " -> v" + nodeId + " [arrowhead=none]");
		}
	}

	private static String nodeName(final Tree node, final int nodeId, final VariableRegistry vars) {
		if (node.isLeaf()) {
			if (node instanceof VariableLeaf) {
				final Variable var = ((VariableLeaf) node).getVariable();
				if (vars.exists(var)) {
					return vars.name(var);
				} else {
					return letter(var);
				}
			} else {
				return "VALUE";
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

	public static <T> void dump(final DecompositionASDD<T> asdd, final VariableRegistry vars, final String fileName) throws FileNotFoundException {
		GraphvizDumper.setOutput(new PrintStream(fileName));
		dump(asdd, vars);
	}

	public static <T> void dump(final DecompositionASDD<T> asdd, final VariableRegistry vars) {
		out.println("digraph sdd {");
		out.println("  graph [ordering=\"out\"]");
		final Map<Tree, Integer> vtreeMap = new HashMap<Tree, Integer>();
		dumpVTreeNode(asdd.getTree(), vtreeMap, vars);
		dumpAlgebraicDecomposition(asdd, vtreeMap, vars);
		out.println("}");
	}

	private static <T> void dumpAlgebraicDecomposition(final DecompositionASDD<T> asdd, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		dumpAlgebraicDecomposition(asdd, 0, new HashMap<Element, Integer>(), new HashMap<AlgebraicElement<T>, Integer>(), new HashMap<DecompositionSDD, Integer>(), new HashMap<ASDD<T>, Integer>(), vtreeMap, vars);
	}

	private static <T> int dumpAlgebraicDecomposition(final ASDD<T> asdd, final int nextId, final Map<Element, Integer> elemCache, final Map<AlgebraicElement<T>, Integer> algebraicElemCache, final Map<DecompositionSDD, Integer> decompCache, final Map<ASDD<T>, Integer> algebraicCache, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!algebraicCache.containsKey(asdd)) {
			final int decompId = nextId;
			algebraicCache.put(asdd, decompId);
			if (asdd.isTerminal()) {
				out.println("  d" + decompId + " [style=filled,fillcolor=\"" + COLOR_VAL + "\",label=\"" + asdd.toString() + "\"]");
				return decompId + 1;
			} else {
				out.println("  d" + decompId + " [style=filled,fillcolor=\"" + COLOR_DECOMP_AL + "\",shape=circle,label=\"" + vtreeMap.get(asdd.getTree()) + "\"]");
				final List<Integer> ids = new ArrayList<Integer>();
				int id = nextId + 1;
				for (final AlgebraicElement<T> element : ((DecompositionASDD<T>) asdd).getElements()) {
					id = dumpAlgebraicPairedBox(element, id, elemCache, algebraicElemCache, decompCache, algebraicCache, decompId, vtreeMap, vars);
					ids.add(algebraicElemCache.get(element));
				}
				out.print("  { rank=same;");
				for (final int eid : ids) {
					out.print(" e" + eid + ";");
				}
				out.println(" }");
				return id;
			}
		} else {
			return nextId;
		}
	}

	private static <T> int dumpAlgebraicPairedBox(final AlgebraicElement<T> element, final int nextId, final Map<Element, Integer> elemCache, final Map<AlgebraicElement<T>, Integer> algebraicElemCache, final Map<DecompositionSDD, Integer> decompCache, final Map<ASDD<T>, Integer> algebraicCache, final int decompId, final Map<Tree, Integer> vtreeMap, final VariableRegistry vars) {
		if (!algebraicElemCache.containsKey(element)) {
			final SDD prime = element.getPrime();
			final ASDD<T> sub = element.getSub();
			final String primeLabel = label(prime, vars);
			final String subLabel = "&#9679;";
			int leftId = nextId;
			if (!prime.isTerminal()) {
				leftId = dumpDecomposition((DecompositionSDD) prime, nextId, elemCache, decompCache, vtreeMap, vars);
			}
			int rightId = dumpAlgebraicDecomposition(sub, leftId, elemCache, algebraicElemCache, decompCache, algebraicCache, vtreeMap, vars);
			final int elementId = rightId + 1;
			algebraicElemCache.put(element, elementId);
			out.println("  e" + elementId + " [style=filled,fillcolor=\"" + COLOR_ELEM_AL + "\",shape=record,label=\"<f0> " + primeLabel + "|<f1> " + subLabel + "\"]");
			if (!prime.isTerminal()) {
				out.println("  e" + elementId + ":f0:c -> d" + decompCache.get(prime) + " [tailclip=false]");
			}
			out.println("  e" + elementId + ":f1:c -> d" + algebraicCache.get(sub) + " [tailclip=false]");
			out.println("  d" + decompId + " -> e" + elementId);
			return elementId;
		} else {
			final int elementId = algebraicElemCache.get(element);
			out.println("  d" + decompId + " -> e" + elementId);
			return nextId;
		}
	}

}
