package jasdd.viz;

import jasdd.algebraic.ASDD;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.ConstantSDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.bool.LiteralSDD;
import jasdd.bool.SDD;
import jasdd.logic.AssociativeConnectorFormula;
import jasdd.logic.Conjunction;
import jasdd.logic.Disjunction;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.TerminalFormula;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.vtree.InternalTree;
import jasdd.vtree.Tree;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private static final String ELEM_HEIGHT   = "0.4";
	private static final String DECOMP_HEIGHT = "0.5";

	public static PrintStream out = System.out;

	public static void setOutput(final String fileName) throws FileNotFoundException {
		setOutput(new PrintStream(fileName));
	}

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
			out.println("  d" + decompId + " [style=filled,fillcolor=\"" + COLOR_DECOMP + "\",shape=circle,height=\"" + DECOMP_HEIGHT + "\",label=\"" + vtreeMap.get(sdd.getVTree()) + "\"]");
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
				return ((ConstantSDD) sdd).getSign() ? "&#8868;" : "&#8869;";
			} else {
				final Literal literal = ((LiteralSDD) sdd).getLiteral();
				final Variable var = literal.getVariable();
				return (literal.getSign() ? "" : "&#172;") + (vars.exists(var) ? vars.name(var) : letter(literal.getVariable()));
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
		return "" + (char) ('A' + variable.getIndex());
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
		out.println("  node [height=" + ELEM_HEIGHT + ",margin=0.05,0.05]");
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
				out.println("  d" + decompId + " [style=filled,fillcolor=\"" + COLOR_DECOMP_AL + "\",shape=circle,height=\"" + DECOMP_HEIGHT + "\",label=\"" + vtreeMap.get(asdd.getTree()) + "\"]");
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
			final int rightId = dumpAlgebraicDecomposition(sub, leftId, elemCache, algebraicElemCache, decompCache, algebraicCache, vtreeMap, vars);
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

	public static String formulaSymbol(final Formula formula) {
		if (formula instanceof Conjunction) {
			return "&#8743;";
		} else if (formula instanceof Disjunction) {
			return "&#8744;";
		} else {
			return "&#8855;";
		}
	}

	public static void dump(final Formula formula, final String fileName) throws FileNotFoundException {
		final PrintStream oldOut = out;
		setOutput(fileName);
		dump(formula);
		out = oldOut;
	}

	public static void dump(final Formula formula) {
		out.println("digraph f {");
		dump(formula, 0, -1);
		out.println("}");
	}

	public static int dump(final Formula formula, final int nextId, final int parentId) {
		if (parentId >= 0) {
			out.println(parentId + " -> " + nextId);
		}
		if (formula instanceof TerminalFormula) {
			out.println(nextId + " [label=\"" + formula + "\"]");
			return nextId + 1;
		} else {
			out.println(nextId + " [label=\"" + formulaSymbol(formula) + "\"]");
			final Set<Formula> subs = ((AssociativeConnectorFormula) formula).getFormulas();
			final int parent = nextId;
			int id = nextId + 1;
			for (final Formula sub : subs) {
				id = dump(sub, id, parent);
			}
			return id;
		}
	}

}
