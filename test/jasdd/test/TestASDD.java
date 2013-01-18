package jasdd.test;

import jasdd.algebraic.AlgebraicElement;
import jasdd.algebraic.AlgebraicTerminal;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.ValueLeaf;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;

import junit.framework.Assert;

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

		final AlgebraicTerminal<Float> high = new AlgebraicTerminal<Float>(0.9f); 
		final AlgebraicTerminal<Float> low = new AlgebraicTerminal<Float>(0.1f); 

		final DecompositionASDD<Float> a22  = new DecompositionASDD<Float>(ll4, new AlgebraicElement<Float>(vars.register("alive(x2,y2)"), high), new AlgebraicElement<Float>(vars.register("alive(x2,y2)"), false, low ));
		final DecompositionASDD<Float> a212 = new DecompositionASDD<Float>(ll3, new AlgebraicElement<Float>(vars.register("alive(x2,y1)"), high), new AlgebraicElement<Float>(vars.register("alive(x2,y1)"), false, a22 ));
		final DecompositionASDD<Float> a211 = new DecompositionASDD<Float>(ll3, new AlgebraicElement<Float>(vars.register("alive(x2,y1)"), a22) , new AlgebraicElement<Float>(vars.register("alive(x2,y1)"), false, low ));
		final DecompositionASDD<Float> a122 = new DecompositionASDD<Float>(ll2, new AlgebraicElement<Float>(vars.register("alive(x1,y2)"), a212), new AlgebraicElement<Float>(vars.register("alive(x1,y2)"), false, a211));
		final DecompositionASDD<Float> a121 = new DecompositionASDD<Float>(ll2, new AlgebraicElement<Float>(vars.register("alive(x1,y2)"), a211), new AlgebraicElement<Float>(vars.register("alive(x1,y2)"), false, low ));
		final DecompositionASDD<Float> a11  = new DecompositionASDD<Float>(ll1, new AlgebraicElement<Float>(vars.register("alive(x1,y1)"), a122), new AlgebraicElement<Float>(vars.register("alive(x1,y1)"), false, a121));

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

		final AlgebraicTerminal<Float> high = new AlgebraicTerminal<Float>(0.9f); 
		final AlgebraicTerminal<Float> low = new AlgebraicTerminal<Float>(0.1f); 

		final DecompositionASDD<Float> a22 = new DecompositionASDD<Float>(
			(InternalAVTree) right.getRight(),
			new AlgebraicElement<Float>(x2y2, high),
			new AlgebraicElement<Float>(x2y2, false, low)
		);
		final DecompositionASDD<Float> a212 = new DecompositionASDD<Float>(
			right,
			new AlgebraicElement<Float>(x2y1, high),
			new AlgebraicElement<Float>(x2y1, false, a22)
		);
		final DecompositionASDD<Float> a211 = new DecompositionASDD<Float>(
			right,
			new AlgebraicElement<Float>(x2y1, a22),
			new AlgebraicElement<Float>(x2y1, false, low)
		);
		final DecompositionSDD part1 = new DecompositionSDD(
			left,
			new Element(x1y1, x1y2),
			new Element(x1y1, false, false)
		);
		final DecompositionSDD part2 = new DecompositionSDD(
			left,
			new Element(x1y1, x1y2, false),
			new Element(x1y1, false, x1y2)
		);
		final DecompositionSDD part3 = new DecompositionSDD(
			left,
			new Element(x1y1, false),
			new Element(x1y1, false, x1y2, false)
		);
		final DecompositionASDD<Float> asdd = new DecompositionASDD<Float>(
			root,
			new AlgebraicElement<Float>(part1, a212),
			new AlgebraicElement<Float>(part2, a211),
			new AlgebraicElement<Float>(part3, low)
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

		final AlgebraicTerminal<Float> high = new AlgebraicTerminal<Float>(0.9f); 
		final AlgebraicTerminal<Float> low = new AlgebraicTerminal<Float>(0.1f); 

		@SuppressWarnings("unchecked")
		final DecompositionASDD<Float> asdd =
		new DecompositionASDD<Float>(root,
			new AlgebraicElement<Float>(
				new DecompositionSDD(upperLeft, Element.shannon(x1y1, x1y2, false)),
				new DecompositionASDD<Float>(right,
					new AlgebraicElement<Float>(
						new DecompositionSDD(lowerLeft, Element.shannon(x2y1, true, x2y2)), high),
						new AlgebraicElement<Float>(
							new DecompositionSDD(lowerLeft, Element.shannon(x2y1, false, x2y2, false)), low))),
			new AlgebraicElement<Float>(
				new DecompositionSDD(upperLeft, Element.shannon(x1y1, x1y2, false, x1y2)),
				new DecompositionASDD<Float>(right,
					new AlgebraicElement<Float>(
						new DecompositionSDD(lowerLeft, Element.shannon(x2y1, x2y2, false)), high),
						new AlgebraicElement<Float>(
							new DecompositionSDD(lowerLeft, Element.shannon(x2y1, x2y2, false, true)), low))),
			new AlgebraicElement<Float>(
				new DecompositionSDD(upperLeft, Element.shannon(x1y1, false, x1y2, false)), low));

		Assert.assertEquals(21, asdd.size());

		GraphvizDumper.dump(asdd, vars, "asdd.gv");
	}

}