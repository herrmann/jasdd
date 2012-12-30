package jsdd.test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jsdd.Variable;
import jsdd.VariableRegistry;
import jsdd.rddlsim.ASDDConverter;
import jsdd.rddlsim.ASDDConverter.Enumeration;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalRightLinearAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.ValueLeaf;
import jsdd.vtree.VariableLeaf;
import junit.framework.Assert;

import org.junit.Test;

/**
 * VTree construction tests.
 * 
 * @author Ricardo Herrmann
 */
public class VTreeTest {

	@Test
	public void basicSyntax() {
		final VariableRegistry vars = new VariableRegistry();
		final VariableLeaf a = new VariableLeaf(vars.register("A"));
		final ValueLeaf value = new ValueLeaf();

		new InternalAVTree(a, value);

//		The following lines must not compile
//		new InternalAVTree(value, a);
//		new InternalAVTree(a, a);
//		new InternalAVTree(value, value);

		new InternalVTree(a, a);

		new InternalRightLinearAVTree(a, value);

//		The following lines must not compile
//		new InternalRightLinearAVTree(value, a);
//		new InternalRightLinearAVTree(a, a);
//		new InternalRightLinearAVTree(value, value);
	}

	@Test
	public void gameOfLife1() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalRightLinearAVTree vtree = new InternalRightLinearAVTree(
			new VariableLeaf(vars.register("alive(x1,y1)")),
			new InternalRightLinearAVTree(
				new VariableLeaf(vars.register("alive(x1,y2)")),
				new InternalRightLinearAVTree(
					new VariableLeaf(vars.register("alive(x2,y1)")),
					new InternalRightLinearAVTree(
						new VariableLeaf(vars.register("alive(x2,y2)")),
						new ValueLeaf()
					)
				)
			)
		);
		Assert.assertEquals(new ValueLeaf(), vtree.rightmostLeaf());
		Assert.assertEquals(new VariableLeaf(vars.register("alive(x1,y1)")), vtree.leftmostLeaf());
		Assert.assertEquals(new VariableLeaf(vars.register("alive(x1,y2)")), vtree.getRight().leftmostLeaf());
	}

	@Test
	public void gameOfLife2() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalAVTree vtree = new InternalAVTree(
			new InternalVTree(
				new VariableLeaf(vars.register("alive(x1,y1)")),
				new VariableLeaf(vars.register("alive(x1,y2)"))
			),
			new InternalRightLinearAVTree(
				new VariableLeaf(vars.register("alive(x2,y1)")),
				new InternalRightLinearAVTree(
					new VariableLeaf(vars.register("alive(x2,y2)")),
					new ValueLeaf()
				)
			)
		);
		Assert.assertEquals(new ValueLeaf(), vtree.rightmostLeaf());
		Assert.assertEquals(new VariableLeaf(vars.register("alive(x1,y1)")), vtree.leftmostLeaf());
		Assert.assertEquals(new VariableLeaf(vars.register("alive(x2,y1)")), vtree.getRight().leftmostLeaf());
	}

	@Test
	public void gameOfLife3() {
		final VariableRegistry vars = new VariableRegistry();
		new InternalAVTree(
			new InternalVTree(
				new VariableLeaf(vars.register("alive(x1,y1)")),
				new VariableLeaf(vars.register("alive(x1,y2)"))
			),
			new InternalAVTree(
				new InternalVTree(
					new VariableLeaf(vars.register("alive(x2,y1)")),
					new VariableLeaf(vars.register("alive(x2,y2)"))
				),
				new ValueLeaf()
			)
		);
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
		final Enumeration counter = new ASDDConverter.Enumeration() {
			@Override
			public void values(final Map<Variable, Boolean> values) {
				count.incrementAndGet();
			}
		};
		converter.convert(root, counter);
		Assert.assertEquals(16, count.get());
	}

}
