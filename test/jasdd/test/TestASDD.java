package jasdd.test;

import jasdd.algebraic.ASDD;
import jasdd.algebraic.ASDDFactory;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicOperatorApplication;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.CachingASDDFactory;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.CachingSDDFactory;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDDFactory;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.stat.Summary;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.ValueLeaf;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Integration tests for SDD construction and manipulation.
 *
 * @author Ricardo Herrmann
 */
public class TestASDD {

	private final ASDDFactory algFactory = CachingASDDFactory.getInstance();

	@Test
	@SuppressWarnings("unchecked")
	public void rightLinearGameOfLife() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		for (int x = 1; x <= 2; x++) {
			for (int y = 1; y <= 2; y++) {
				final String name = "alive(x" + x + ",y" + y + ")";
				vars.register(name);
			}
		}
		vars.register("value");

		final InternalAVTree ll4 = new InternalAVTree(new VariableLeaf(vars.register("alive(x1,y2)")), new ValueLeaf());
		final InternalAVTree ll3 = new InternalAVTree(new VariableLeaf(vars.register("alive(x1,y2)")), ll4);
		final InternalAVTree ll2 = new InternalAVTree(new VariableLeaf(vars.register("alive(x1,y2)")), ll3);
		final InternalAVTree ll1 = new InternalAVTree(new VariableLeaf(vars.register("alive(x1,y1)")), ll2);

		final AlgebraicTerminal<Float> high = algFactory.createTerminal(0.9f);
		final AlgebraicTerminal<Float> low = algFactory.createTerminal(0.1f);

		final DecompositionASDD<Float> a22  = algFactory.createDecomposition(ll4, algFactory.createElement(vars.register("alive(x2,y2)"), high), algFactory.createElement(vars.register("alive(x2,y2)"), false, low ));
		final DecompositionASDD<Float> a212 = algFactory.createDecomposition(ll3, algFactory.createElement(vars.register("alive(x2,y1)"), high), algFactory.createElement(vars.register("alive(x2,y1)"), false, a22 ));
		final DecompositionASDD<Float> a211 = algFactory.createDecomposition(ll3, algFactory.createElement(vars.register("alive(x2,y1)"), a22) , algFactory.createElement(vars.register("alive(x2,y1)"), false, low ));
		final DecompositionASDD<Float> a122 = algFactory.createDecomposition(ll2, algFactory.createElement(vars.register("alive(x1,y2)"), a212), algFactory.createElement(vars.register("alive(x1,y2)"), false, a211));
		final DecompositionASDD<Float> a121 = algFactory.createDecomposition(ll2, algFactory.createElement(vars.register("alive(x1,y2)"), a211), algFactory.createElement(vars.register("alive(x1,y2)"), false, low ));
		final DecompositionASDD<Float> a11  = algFactory.createDecomposition(ll1, algFactory.createElement(vars.register("alive(x1,y1)"), a122), algFactory.createElement(vars.register("alive(x1,y1)"), false, a121));

		Assert.assertEquals(12, a11.size());

		GraphvizDumper.dump(a11, vars, "sdd.gv");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void pseudoLinearGameOfLife() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		vars.register("value");
		final Variable x1y1 = vars.register("alive(x1,y1)");
		final Variable x1y2 = vars.register("alive(x1,y2)");
		final Variable x2y1 = vars.register("alive(x2,y1)");
		final Variable x2y2 = vars.register("alive(x2,y2)");

		final InternalVTree left = new InternalVTree(
			new VariableLeaf(x1y1),
			new VariableLeaf(x1y2)
		);
		final InternalAVTree right = new InternalAVTree(
			new VariableLeaf(x2y1),
			new InternalAVTree(
				new VariableLeaf(x2y2),
				new ValueLeaf()
			)
		);
		final InternalAVTree root = new InternalAVTree(left, right);

		final AlgebraicTerminal<Float> high = algFactory.createTerminal(0.9f);
		final AlgebraicTerminal<Float> low = algFactory.createTerminal(0.1f);

		final SDDFactory factory = CachingSDDFactory.getInstance();

		final DecompositionASDD<Float> a22 = algFactory.createDecomposition(
			(InternalAVTree) right.getRight(),
			algFactory.createElement(x2y2, high),
			algFactory.createElement(x2y2, false, low)
		);
		final DecompositionASDD<Float> a212 = algFactory.createDecomposition(
			right,
			algFactory.createElement(x2y1, high),
			algFactory.createElement(x2y1, false, a22)
		);
		final DecompositionASDD<Float> a211 = algFactory.createDecomposition(
			right,
			algFactory.createElement(x2y1, a22),
			algFactory.createElement(x2y1, false, low)
		);
		final DecompositionSDD part1 = factory.createDecomposition(
			left,
			factory.createElement(x1y1, x1y2),
			factory.createElement(x1y1, false, false)
		);
		final DecompositionSDD part2 = factory.createDecomposition(
			left,
			factory.createElement(x1y1, x1y2, false),
			factory.createElement(x1y1, false, x1y2)
		);
		final DecompositionSDD part3 = factory.createDecomposition(
			left,
			factory.createElement(x1y1, false),
			factory.createElement(x1y1, false, x1y2, false)
		);
		final DecompositionASDD<Float> asdd = algFactory.createDecomposition(
			root,
			algFactory.createElement(part1, a212),
			algFactory.createElement(part2, a211),
			algFactory.createElement(part3, low)
		);

		Assert.assertEquals(15, asdd.size());

		GraphvizDumper.dump(asdd, vars, "asdd.gv");
	}

	@Test
	public void groupsOfTwoGameOfLife() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		vars.register("value");
		final Variable x1y1 = vars.register("alive(x1,y1)");
		final Variable x1y2 = vars.register("alive(x1,y2)");
		final Variable x2y1 = vars.register("alive(x2,y1)");
		final Variable x2y2 = vars.register("alive(x2,y2)");

		final InternalVTree upperLeft = new InternalVTree(x1y1, x1y2);
		final InternalVTree lowerLeft = new InternalVTree(x2y1, x2y2);
		final InternalAVTree right = new InternalAVTree(lowerLeft);
		final InternalAVTree root = new InternalAVTree(upperLeft, right);

		final AlgebraicTerminal<Float> high = algFactory.createTerminal(0.9f);
		final AlgebraicTerminal<Float> low = algFactory.createTerminal(0.1f);

		final SDDFactory factory = CachingSDDFactory.getInstance();

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Float> asdd =
		algFactory.createDecomposition(root,
			algFactory.createElement(
					factory.createDecomposition(upperLeft, factory.shannon(x1y1, x1y2, false)),
				algFactory.createDecomposition(right,
					algFactory.createElement(
							factory.createDecomposition(lowerLeft, factory.shannon(x2y1, true, x2y2)), high),
						algFactory.createElement(
								factory.createDecomposition(lowerLeft, factory.shannon(x2y1, false, x2y2, false)), low))),
			algFactory.createElement(
					factory.createDecomposition(upperLeft, factory.shannon(x1y1, x1y2, false, x1y2)),
				algFactory.createDecomposition(right,
					algFactory.createElement(
							factory.createDecomposition(lowerLeft, factory.shannon(x2y1, x2y2, false)), high),
						algFactory.createElement(
								factory.createDecomposition(lowerLeft, factory.shannon(x2y1, x2y2, false, true)), low))),
			algFactory.createElement(
					factory.createDecomposition(upperLeft, factory.shannon(x1y1, false, x1y2, false)), low));

		Assert.assertEquals(21, asdd.size());

		GraphvizDumper.dump(asdd, vars, "asdd.gv");
	}

	@Test
	public void sumTerminals() {
		final ASDD<Double> left = algFactory.createTerminal(1.0);
		final ASDD<Double> right = algFactory.createTerminal(2.0);
		final ASDD<Double> result = AlgebraicOperatorApplication.sum(left, right);
		Assert.assertEquals(algFactory.createTerminal(3.0), result);
	}

	@Test
	public void sumScalar() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final ASDD<Double> low = algFactory.createTerminal(1.0);
		final ASDD<Double> high = algFactory.createTerminal(2.0);
		final AlgebraicElement<Double>[] elements = algFactory.shannon(a, high, low);
		final InternalAVTree avtree = new InternalAVTree(a, new ValueLeaf());
		final ASDD<Double> asdd = algFactory.createDecomposition(avtree, elements);
		final ASDD<Double> increment = algFactory.createTerminal(0.5);
		final ASDD<Double> result = AlgebraicOperatorApplication.sum(asdd, increment);
		Assert.assertEquals("[(0,VALUE), ((0 /\\ 2.5) \\/ (-0 /\\ 1.5))]", result.toString());
	}

	@Test
	public void sumDecompositions() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");

		final InternalAVTree subtree = new InternalAVTree(b, new ValueLeaf());
		final InternalAVTree avtree = new InternalAVTree(a, subtree);

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countA = algFactory.createDecomposition(avtree, algFactory.shannon(a,
			algFactory.createDecomposition(subtree, algFactory.createElement(algFactory.createTerminal(1.0))),
			algFactory.createDecomposition(subtree, algFactory.createElement(algFactory.createTerminal(0.0)))));

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countB = algFactory.createDecomposition(avtree, algFactory.createElement(
			algFactory.createDecomposition(subtree, algFactory.shannon(b, algFactory.createTerminal(1.0), algFactory.createTerminal(0.0)))));

		final DecompositionASDD<Double> result = (DecompositionASDD<Double>) AlgebraicOperatorApplication.sum(countA, countB);

		Assert.assertEquals("[(0,(1,VALUE)), ((0 /\\ [(1,VALUE), ((1 /\\ 2.0) \\/ (-1 /\\ 1.0))]) \\/ (-0 /\\ [(1,VALUE), ((1 /\\ 1.0) \\/ (-1 /\\ 0.0))]))]", result.toString());
	}

	private DecompositionASDD<Double> leftLinearExample() {
		return leftLinearExample(new VariableRegistry());
	}

	private DecompositionASDD<Double> leftLinearExample(final VariableRegistry vars) {
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");

		final InternalVTree subtree = new InternalVTree(a, b);
		final InternalAVTree avtree = new InternalAVTree(subtree, new ValueLeaf());

		final SDDFactory factory = CachingSDDFactory.getInstance();

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countA = algFactory.createDecomposition(avtree,
			algFactory.createElement(factory.createDecomposition(subtree, factory.shannon(a, true, false)), algFactory.createTerminal(1.0)),
			algFactory.createElement(factory.createDecomposition(subtree, factory.shannon(a, false, true)), algFactory.createTerminal(0.0)));

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countB = algFactory.createDecomposition(avtree,
			algFactory.createElement(factory.createDecomposition(subtree, factory.createElement(true, b)), algFactory.createTerminal(1.0)),
			algFactory.createElement(factory.createDecomposition(subtree, factory.createElement(true, b, false)), algFactory.createTerminal(0.0)));

		final DecompositionASDD<Double> result = (DecompositionASDD<Double>) AlgebraicOperatorApplication.sum(countA, countB);
		return result;
	}

	@Test
	public void sumDecompositionsLeftLinear() {
		final DecompositionASDD<Double> result = leftLinearExample();
		Assert.assertEquals("[((0,1),VALUE), (([(0,1), ((0 /\\ 1) \\/ (-0 /\\ F))] /\\ 2.0) \\/ ([(0,1), ((0 /\\ -1) \\/ (-0 /\\ 1))] /\\ 1.0) \\/ ([(0,1), ((0 /\\ F) \\/ (-0 /\\ -1))] /\\ 0.0))]", result.toString());
	}

	@Test
	public void nothingToTrim() {
		final DecompositionASDD<Double> result = leftLinearExample();
		Assert.assertEquals(result, result.trimmed());
	}

	@Test
	public void stats() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		final DecompositionASDD<Double> result = leftLinearExample(vars);
		final Summary stats = Summary.from(result);
		Assert.assertEquals(3, stats.getAlgebraicTerminals());
		Assert.assertEquals(3, stats.getAlgebraicElements());
		Assert.assertEquals(6, stats.getElements());
		Assert.assertEquals(1, stats.getAlgebraicDecompositions());
		Assert.assertEquals(3, stats.getDecompositions());
		Assert.assertEquals(2, stats.getDepth());
		Assert.assertEquals(9, result.size());
	}

	@Test
	public void terminals() {
		final DecompositionASDD<Double> result = leftLinearExample();
		final Set<Double> terminals = result.terminals();
		Assert.assertEquals(3, terminals.size());
		for (int i = 0; i < 3; i++) {
			terminals.contains(i);
		}
	}

}
