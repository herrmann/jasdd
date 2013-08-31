package jasdd.test;

import jasdd.JASDD;
import jasdd.algebraic.ASDD;
import jasdd.algebraic.DecompositionASDD;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.rddlsim.ASDDConverter;
import jasdd.rddlsim.ASDDConverter.VariableAssignment;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.ValueLeaf;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import dd.discrete.ADD;

/**
 * Tests that depend on rddlsim.
 * 
 * @author Ricardo Herrmann
 */
public class RddlsimTest {

	@Test
	public void fromConstantADD() {
	    final ArrayList<String> order = new ArrayList<String>();
	    final ADD context = new ADD(order);
	    final int node = context.getConstantNode(1.0);
	    context.getNode(node);
	}

	@Test
	public void fromADD() throws FileNotFoundException {
	    final ArrayList<String> order = new ArrayList<String>();
	    order.add("A");
	    order.add("B");
	    final ADD context = new ADD(order);
	    final int nodeId = context.getVarNode("A", 1.0, 2.0);
	    final ASDDConverter converter = new ASDDConverter(context);
	    final VariableRegistry vars = new VariableRegistry();
	    final Variable a = vars.register("A");
	    final Variable b = vars.register("B");

	    final InternalAVTree subavtree = new InternalAVTree(a, new ValueLeaf());
		final InternalAVTree avtree = new InternalAVTree(b, subavtree);

	    final ASDD<Double> asdd = converter.convert(nodeId, avtree, vars);

	    @SuppressWarnings("unchecked")
		final DecompositionASDD<Double> result =
	    JASDD.createDecomposition(avtree,
	    	JASDD.createElement(
	    		JASDD.createTrue(),
	    		JASDD.createDecomposition(subavtree,
	    			JASDD.shannon(a,
	    				JASDD.createTerminal(2.0),
	    				JASDD.createTerminal(1.0)))));
	    GraphvizDumper.dump(result, vars, "expected.gv");
	}

	@Test
	public void partitionsEnumeration() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable x1y1 = vars.register("alive(x1,y1)");
		final Variable x1y2 = vars.register("alive(x1,y2)");
		final Variable x2y1 = vars.register("alive(x2,y1)");
		final Variable x2y2 = vars.register("alive(x2,y2)");

		final InternalAVTree root = new InternalAVTree(
			new InternalVTree(
				new InternalVTree(x1y1, x1y2),
				new InternalVTree(x2y1, x2y2)));

		final ASDDConverter converter = new ASDDConverter();
		final AtomicInteger count = new AtomicInteger();
		final VariableAssignment counter = new ASDDConverter.VariableAssignment() {
			@Override
			public void assignment(final Map<Variable, Boolean> values) {
				count.incrementAndGet();
			}
		};
		converter.convert(root, counter);
		Assert.assertEquals(16, count.get());
	}

}
