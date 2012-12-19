package jsdd.test;

import java.io.FileNotFoundException;

import jsdd.VariableRegistry;
import jsdd.algebraic.AlgebraicElement;
import jsdd.algebraic.AlgebraicTerminal;
import jsdd.algebraic.DecompositionASDD;
import jsdd.viz.GraphvizDumper;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.ValueLeaf;
import jsdd.vtree.VariableLeaf;

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

		GraphvizDumper.dump(a11, vars, "sdd.gv");
	}

}
