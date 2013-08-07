package jasdd.test;

import jasdd.JASDD;
import jasdd.algebraic.ASDD;
import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicOperatorApplication;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.DecompositionSDD;
import jasdd.logic.Formula;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.stat.Summary;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.ValueLeaf;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests for SDD construction and manipulation.
 *
 * @author Ricardo Herrmann
 */
public class TestASDD {

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

		final AlgebraicTerminal<Float> high = JASDD.createTerminal(0.9f);
		final AlgebraicTerminal<Float> low = JASDD.createTerminal(0.1f);

		final DecompositionASDD<Float> a22  = JASDD.createDecomposition(ll4, JASDD.createElement(vars.register("alive(x2,y2)"), high), JASDD.createElement(vars.register("alive(x2,y2)"), false, low ));
		final DecompositionASDD<Float> a212 = JASDD.createDecomposition(ll3, JASDD.createElement(vars.register("alive(x2,y1)"), high), JASDD.createElement(vars.register("alive(x2,y1)"), false, a22 ));
		final DecompositionASDD<Float> a211 = JASDD.createDecomposition(ll3, JASDD.createElement(vars.register("alive(x2,y1)"), a22) , JASDD.createElement(vars.register("alive(x2,y1)"), false, low ));
		final DecompositionASDD<Float> a122 = JASDD.createDecomposition(ll2, JASDD.createElement(vars.register("alive(x1,y2)"), a212), JASDD.createElement(vars.register("alive(x1,y2)"), false, a211));
		final DecompositionASDD<Float> a121 = JASDD.createDecomposition(ll2, JASDD.createElement(vars.register("alive(x1,y2)"), a211), JASDD.createElement(vars.register("alive(x1,y2)"), false, low ));
		final DecompositionASDD<Float> a11  = JASDD.createDecomposition(ll1, JASDD.createElement(vars.register("alive(x1,y1)"), a122), JASDD.createElement(vars.register("alive(x1,y1)"), false, a121));

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

		final AlgebraicTerminal<Float> high = JASDD.createTerminal(0.9f);
		final AlgebraicTerminal<Float> low = JASDD.createTerminal(0.1f);

		final DecompositionASDD<Float> a22 = JASDD.createDecomposition(
			(InternalAVTree) right.getRight(),
			JASDD.createElement(x2y2, high),
			JASDD.createElement(x2y2, false, low)
		);
		final DecompositionASDD<Float> a212 = JASDD.createDecomposition(
			right,
			JASDD.createElement(x2y1, high),
			JASDD.createElement(x2y1, false, a22)
		);
		final DecompositionASDD<Float> a211 = JASDD.createDecomposition(
			right,
			JASDD.createElement(x2y1, a22),
			JASDD.createElement(x2y1, false, low)
		);
		final DecompositionSDD part1 = JASDD.createDecomposition(
			left,
			JASDD.createElement(x1y1, x1y2),
			JASDD.createElement(x1y1, false, false)
		);
		final DecompositionSDD part2 = JASDD.createDecomposition(
			left,
			JASDD.createElement(x1y1, x1y2, false),
			JASDD.createElement(x1y1, false, x1y2)
		);
		final DecompositionSDD part3 = JASDD.createDecomposition(
			left,
			JASDD.createElement(x1y1, false),
			JASDD.createElement(x1y1, false, x1y2, false)
		);
		final DecompositionASDD<Float> asdd = JASDD.createDecomposition(
			root,
			JASDD.createElement(part1, a212),
			JASDD.createElement(part2, a211),
			JASDD.createElement(part3, low)
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

		final AlgebraicTerminal<Float> high = JASDD.createTerminal(0.9f);
		final AlgebraicTerminal<Float> low = JASDD.createTerminal(0.1f);

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Float> asdd =
		JASDD.createDecomposition(root,
			JASDD.createElement(
					JASDD.createDecomposition(upperLeft, JASDD.shannon(x1y1, x1y2, false)),
				JASDD.createDecomposition(right,
					JASDD.createElement(
							JASDD.createDecomposition(lowerLeft, JASDD.shannon(x2y1, true, x2y2)), high),
						JASDD.createElement(
								JASDD.createDecomposition(lowerLeft, JASDD.shannon(x2y1, false, x2y2, false)), low))),
			JASDD.createElement(
					JASDD.createDecomposition(upperLeft, JASDD.shannon(x1y1, x1y2, false, x1y2)),
				JASDD.createDecomposition(right,
					JASDD.createElement(
							JASDD.createDecomposition(lowerLeft, JASDD.shannon(x2y1, x2y2, false)), high),
						JASDD.createElement(
								JASDD.createDecomposition(lowerLeft, JASDD.shannon(x2y1, x2y2, false, true)), low))),
			JASDD.createElement(
					JASDD.createDecomposition(upperLeft, JASDD.shannon(x1y1, false, x1y2, false)), low));

		Assert.assertEquals(21, asdd.size());

		GraphvizDumper.dump(asdd, vars, "asdd.gv");
	}

	@Test
	public void sumTerminals() {
		final ASDD<Double> left = JASDD.createTerminal(1.0);
		final ASDD<Double> right = JASDD.createTerminal(2.0);
		final ASDD<Double> result = AlgebraicOperatorApplication.sum(left, right);
		Assert.assertEquals(JASDD.createTerminal(3.0), result);
	}

	@Test
	public void sumScalar() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final ASDD<Double> low = JASDD.createTerminal(1.0);
		final ASDD<Double> high = JASDD.createTerminal(2.0);
		final AlgebraicElement<Double>[] elements = JASDD.shannon(a, high, low);
		final InternalAVTree avtree = new InternalAVTree(a, new ValueLeaf());
		final ASDD<Double> asdd = JASDD.createDecomposition(avtree, elements);
		final ASDD<Double> increment = JASDD.createTerminal(0.5);
		final ASDD<Double> result = AlgebraicOperatorApplication.sum(asdd, increment);
		Assert.assertEquals("[(0,VALUE), ((0 /\\ 2.5) \\/ (-0 /\\ 1.5))]", result.toString());
	}

	private DecompositionASDD<Double> rightLinearExample() {
		return rightLinearExample(new VariableRegistry());
	}

	private DecompositionASDD<Double> rightLinearExample(final VariableRegistry vars) {
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");

		final InternalAVTree subtree = new InternalAVTree(b, new ValueLeaf());
		final InternalAVTree avtree = new InternalAVTree(a, subtree);

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countA = JASDD.createDecomposition(avtree, JASDD.shannon(a,
			JASDD.createDecomposition(subtree, JASDD.createElement(JASDD.createTerminal(1.0))),
			JASDD.createDecomposition(subtree, JASDD.createElement(JASDD.createTerminal(0.0)))));

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countB = JASDD.createDecomposition(avtree, JASDD.createElement(
			JASDD.createDecomposition(subtree, JASDD.shannon(b, JASDD.createTerminal(1.0), JASDD.createTerminal(0.0)))));

		final DecompositionASDD<Double> result = (DecompositionASDD<Double>) AlgebraicOperatorApplication.sum(countA, countB);
		return result;
	}

	@Test
	public void sumDecompositionsStructuralCheck() {
		final ASDD<Double> result = rightLinearExample();
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

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countA = JASDD.createDecomposition(avtree,
			JASDD.createElement(JASDD.createDecomposition(subtree, JASDD.shannon(a, true, false)), JASDD.createTerminal(1.0)),
			JASDD.createElement(JASDD.createDecomposition(subtree, JASDD.shannon(a, false, true)), JASDD.createTerminal(0.0)));

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Double> countB = JASDD.createDecomposition(avtree,
			JASDD.createElement(JASDD.createDecomposition(subtree, JASDD.createElement(true, b)), JASDD.createTerminal(1.0)),
			JASDD.createElement(JASDD.createDecomposition(subtree, JASDD.createElement(true, b, false)), JASDD.createTerminal(0.0)));

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

	@Test
	public void vtreeIndependentExtractedFunction() {
		final Map<Double, Formula> function = rightLinearExample().extractFunction();
		Assert.assertEquals(3, function.size());
		Assert.assertEquals(function, leftLinearExample().extractFunction());
	}

	/**
	 * Acceptable numeric error when comparing double precision numbers in tests.
	 */
	private final double epsilon = 0.000001;

	@Test
	public void evaluate1() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = leftLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		trueLiterals.add(vars.register("A"));
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(1.0, value, epsilon);
	}

	@Test
	public void evaluate2() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = leftLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		trueLiterals.add(vars.register("B"));
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(1.0, value, epsilon);
	}

	@Test
	public void evaluate3() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = leftLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(0.0, value, epsilon);
	}

	@Test
	public void evaluate4() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = leftLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		trueLiterals.add(vars.register("A"));
		trueLiterals.add(vars.register("B"));
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(2.0, value, epsilon);
	}

	@Test
	public void evaluate5() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = rightLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		trueLiterals.add(vars.register("A"));
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(1.0, value, epsilon);
	}

	@Test
	public void evaluate6() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = rightLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		trueLiterals.add(vars.register("B"));
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(1.0, value, epsilon);
	}

	@Test
	public void evaluate7() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = rightLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(0.0, value, epsilon);
	}

	@Test
	public void evaluate8() {
		final VariableRegistry vars = new VariableRegistry();
		final ASDD<Double> result = rightLinearExample(vars);
		final Set<Variable> trueLiterals = new HashSet<Variable>();
		trueLiterals.add(vars.register("A"));
		trueLiterals.add(vars.register("B"));
		final double value = result.eval(trueLiterals);
		Assert.assertEquals(2.0, value, epsilon);
	}

	@Test
	public void rotateLeft() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		final DecompositionASDD<Double> asdd = rightLinearExample(vars);
		final DecompositionASDD<Double> rotated = (DecompositionASDD<Double>) asdd.rotateLeft();
		GraphvizDumper.dump(rotated, vars, "algrotated.gv");
		Assert.assertNotNull(rotated);
		Assert.assertEquals(9, rotated.size());
	}

}
