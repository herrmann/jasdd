package jsdd.test;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jsdd.Variable;
import jsdd.VariableRegistry;
import jsdd.rddlsim.ASDDConverter;
import jsdd.rddlsim.ASDDConverter.VariableAssignment;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalRightLinearAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.VTree;
import jsdd.vtree.VTreeUtils;
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
		final VariableAssignment counter = new ASDDConverter.VariableAssignment() {
			@Override
			public void assignment(final Map<Variable, Boolean> values) {
				count.incrementAndGet();
			}
		};
		converter.convert(root, counter);
		Assert.assertEquals(16, count.get());
	}

	@Test
	public void dissections() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Collection<VTree> dissections = VTreeUtils.dissections(a, b, c);
		Assert.assertEquals(2, dissections.size());
		final InternalVTree left = new InternalVTree(a, new InternalVTree(b, c));
		final InternalVTree right = new InternalVTree(new InternalVTree(a, b), c);
		Assert.assertTrue(dissections.contains(left));
		Assert.assertTrue(dissections.contains(right));
	}

	@Test(expected = IllegalArgumentException.class)
	public void noEmptyDissections() {
		VTreeUtils.dissections();
	}

	@Test
	public void dissectionsCount() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Variable d = vars.register("D");
		final Variable e = vars.register("E");
		final Variable f = vars.register("F");
		Assert.assertEquals(1, VTreeUtils.dissections(a).size());
		Assert.assertEquals(1, VTreeUtils.dissections(a, b).size());
		Assert.assertEquals(2, VTreeUtils.dissections(a, b, c).size());
		Assert.assertEquals(5, VTreeUtils.dissections(a, b, c, d).size());
		Assert.assertEquals(14, VTreeUtils.dissections(a, b, c, d, e).size());
		Assert.assertEquals(42, VTreeUtils.dissections(a, b, c, d, e, f).size());
	}

	@Test
	public void nextFrontierWhenDissecting() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Variable d = vars.register("D");
		final Variable e = vars.register("E");
		final Variable f = vars.register("F");
		final Collection<VTree> dissections = VTreeUtils.dissections(a, b, c, d, e, f);
		for (final VTree dissection : dissections) {
			// The tree can never be a leaf because we have more than one variable
			Assert.assertFalse(dissection instanceof VariableLeaf);
			final VariableLeaf leaf = (VariableLeaf) ((InternalVTree) dissection).getRight().leftmostLeaf();
			final Variable var = leaf.getVariable();
			// The variable A won't be present because the frontier can't be empty
			Assert.assertFalse(a.equals(var));
			System.out.println(vars.name(var));
		}
	}

}
