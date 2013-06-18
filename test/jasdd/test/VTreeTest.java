package jasdd.test;

import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.rddlsim.ASDDConverter;
import jasdd.rddlsim.ASDDConverter.VariableAssignment;
import jasdd.vtree.DissectionIterator;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalRightLinearAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.Tree;
import jasdd.vtree.VTreeUtils;
import jasdd.vtree.ValueLeaf;
import jasdd.vtree.VariableLeaf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
		final Collection<Tree> dissections = VTreeUtils.dissections(a, b, c);
		Assert.assertEquals(2, dissections.size());
		final InternalVTree left = new InternalVTree(a, new InternalVTree(b, c));
		final InternalVTree right = new InternalVTree(new InternalVTree(a, b), c);
		Assert.assertTrue(dissections.contains(left));
		Assert.assertTrue(dissections.contains(right));
	}

	@Test
	public void algebraicDissections() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Collection<Tree> dissections = VTreeUtils.algebraicDissections(a, b, c);
		Assert.assertEquals(5, dissections.size());
		final ValueLeaf valueLeaf = new ValueLeaf();
		for (final Tree dissection : dissections) {
			Assert.assertEquals(valueLeaf, dissection.rightmostLeaf());
		}
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
		Assert.assertEquals(catalanNumbers[0], VTreeUtils.dissections(a).size());
		Assert.assertEquals(catalanNumbers[1], VTreeUtils.dissections(a, b).size());
		Assert.assertEquals(catalanNumbers[2], VTreeUtils.dissections(a, b, c).size());
		Assert.assertEquals(catalanNumbers[3], VTreeUtils.dissections(a, b, c, d).size());
		Assert.assertEquals(catalanNumbers[4], VTreeUtils.dissections(a, b, c, d, e).size());
		Assert.assertEquals(catalanNumbers[5], VTreeUtils.dissections(a, b, c, d, e, f).size());
	}

	// From http://oeis.org/A000108
	private final long[] catalanNumbers = new long[] { 1, 1, 2, 5, 14, 42, 132, 429,
			1430, 4862, 16796, 58786, 208012, 742900, 2674440, 9694845,
			35357670, 129644790, 477638700, 1767263190, 6564120420L,
			24466267020L, 91482563640L, 343059613650L, 1289904147324L,
			4861946401452L, 18367353072152L, 69533550916004L, 263747951750360L,
			1002242216651368L, 3814986502092304L };

	@Test
	public void nextFrontierWhenDissecting() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Variable d = vars.register("D");
		final Variable e = vars.register("E");
		final Variable f = vars.register("F");
		final Collection<Tree> dissections = VTreeUtils.dissections(a, b, c, d, e, f);
		for (final Tree dissection : dissections) {
			// The tree can never be a leaf because we have more than one variable
			Assert.assertFalse(dissection instanceof VariableLeaf);
			final VariableLeaf leaf = (VariableLeaf) ((InternalVTree) dissection).getRight().leftmostLeaf();
			final Variable var = leaf.getVariable();
			// The variable A won't be present because the frontier can't be empty
			Assert.assertFalse(a.equals(var));
		}
	}

	@Test
	public void dissectionIterator() {
		for (int n = 1; n <= 5; n++) {
			final Tree[] leaves = new Tree[n + 1];
			for (int i = 0; i < n; i++) {
				leaves[i] = new VariableLeaf(i);
			}
			leaves[n] = new ValueLeaf();
			int count = 0;
			for (@SuppressWarnings("unused") final Tree tree : new DissectionIterator(leaves)) {
				count++;
			}
			Assert.assertEquals(catalanNumbers[n], count);
		}
	}

	@Test
	public void skewedTree() {
		final int n = 200;
		final Tree[] leaves = new Tree[n + 1];
		for (int i = 0; i < n; i++) {
			leaves[i] = new VariableLeaf(i);
		}
		leaves[n] = new ValueLeaf();

		final Set<Tree> trees = new HashSet<Tree>();
		final int skews = 2000;
		for (int i = 0; i < skews; i++) {
			final double factor = (double) i / (skews - 1);
			trees.add(VTreeUtils.skewedAVTree(factor, leaves));
		}
		// System.out.println(trees.size());
	}

	@Test
	public void skewnessDistribution() {
		final int n = 9;
		final Tree[] leaves = new Tree[n + 1];
		for (int i = 0; i < n; i++) {
			leaves[i] = new VariableLeaf(i);
		}
		leaves[n] = new ValueLeaf();

		final Map<Tree, Integer> ids = new HashMap<Tree, Integer>();

		int i = 0;
		for (final Tree tree : VTreeUtils.dissections(leaves)) {
			ids.put(tree, i++);
		}

		final int skews = (int) catalanNumbers[n+1];
		Tree lastTree = null;
		for (i = 0; i < skews; i++) {
			final double factor = (double) i / (skews - 1);
			final Tree tree = VTreeUtils.skewedAVTree(factor, leaves);
			if (!tree.equals(lastTree)) {
				lastTree = tree;
				final Integer id = ids.get(tree);
				if (id != null) {
					// System.out.print("," + id);
				}
			}
		}
		// System.out.println();
	}

	@Test
	public void rotateLeft() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = (InternalVTree) VTreeUtils.buildRightLinear(vars, "A", "B", "C");
		final InternalVTree rotated = vtree.rotateLeft();
		Assert.assertEquals("((A,B),C)", rotated.toString(vars));
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotRotateRight() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = (InternalVTree) VTreeUtils.buildRightLinear(vars, "A", "B", "C");
		vtree.rotateRight();
	}

	@Test
	public void rotateRight() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = (InternalVTree) VTreeUtils.buildLeftLinear(vars, "A", "B", "C");
		final InternalVTree rotated = vtree.rotateRight();
		Assert.assertEquals("(A,(B,C))", rotated.toString(vars));
	}

	@Test(expected = IllegalArgumentException.class)
	public void cannotRotateLeft() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = (InternalVTree) VTreeUtils.buildLeftLinear(vars, "A", "B", "C");
		vtree.rotateLeft();
	}

}
