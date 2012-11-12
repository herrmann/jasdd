package jsdd.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jsdd.AbstractSDD;
import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.InternalNode;
import jsdd.LeafNode;
import jsdd.LiteralSDD;
import jsdd.PairedBox;
import jsdd.SDD;
import jsdd.VTree;
import jsdd.Variable;
import jsdd.viz.GraphvizDumper;

import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests for SDD construction and manipulation.
 *  
 * @author Ricardo Herrmann
 */
public class TestSDD {

	@Test
	public void platformHasSaneSetComparison() {
		final Collection<Integer> l1 = new ArrayList<Integer>();
		l1.add(1);
		l1.add(2);
		final Collection<Integer> l2 = new ArrayList<Integer>();
		l2.add(2);
		l2.add(1);
		Assert.assertTrue(!l1.equals(l2));
		Assert.assertTrue(new HashSet<Integer>(l1).equals(new HashSet<Integer>(l2)));
	}

	@Test
	public void correctConstruction1() {
		final DecompositionSDD sdd = (DecompositionSDD) example1();
		final VTree vtree = sdd.getVTree();
		Assert.assertTrue(!vtree.isRightLinear());
		Assert.assertEquals("((2, 1), (4, 3))", vtree.toString());
	}

	@Test
	public void correctConstruction2() {
		final DecompositionSDD sdd = (DecompositionSDD) example2();
		final VTree vtree = sdd.getVTree();
		Assert.assertTrue(vtree.isRightLinear());
		Assert.assertEquals("(1, (2, (3, 4)))", vtree.toString());
	}

	@Test
	public void booleanPairedBoxSerialization() {
		Assert.assertEquals("[T]", new ConstantSDD(true).expansion().toString());
	}

	@Test
	public void singleLiteralPairedBoxSerialization() {
		Assert.assertEquals("[1, F]", new LiteralSDD(1).expansion().toString());
	}

	@Test
	public void vtreeVariables() {
		final Set<Variable> expected = new HashSet<Variable>();
		for (int i = 1; i <=4 ;i++) {
			expected.add(new Variable(i));
		}
		Assert.assertEquals(expected, vtree1().variables());
	}

	@Test
	public void vtreeLinking() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final InternalNode root = (InternalNode) vtree1();

		final PairedBox n0 = new PairedBox(b, a);
		final PairedBox n1 = new PairedBox(b, false, false);
		final PairedBox n2 = new PairedBox(b, a, false);
		final PairedBox n3 = new PairedBox(d, c);
		final PairedBox n4 = new PairedBox(d, false, false);
		final PairedBox n5 = new PairedBox(AbstractSDD.decomposition(root.getLeft(), n0, n1), true);
		final PairedBox n6 = new PairedBox(AbstractSDD.decomposition(root.getLeft(), n1, n2), c);
		final PairedBox n7 = new PairedBox(b, false, AbstractSDD.decomposition(root.getRight(), n3, n4));

		final SDD sdd = AbstractSDD.decomposition(root, n5, n6, n7);

		Assert.assertEquals(root.getLeft(), n0.getVTree());
		Assert.assertEquals(root.getLeft(), n0.getPrime().getVTree());
		Assert.assertEquals(root.getLeft(), n0.getSub().getVTree());
		Assert.assertEquals(root.getLeft(), n1.getSub().getVTree());
		Assert.assertEquals(root.getLeft(), n1.getSub().getVTree());
		Assert.assertEquals(root, n7.getVTree());
		Assert.assertEquals(root, sdd.getVTree());
	}

	@Test
	public void andOperation() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final VTree vtree = new InternalNode(a, b);

		// A /\ -B
		final SDD sdd1 = AbstractSDD.decomposition(vtree, new PairedBox(a, b, false), new PairedBox(a, false, false));

		// -A /\ B
		final SDD sdd2 = AbstractSDD.decomposition(vtree, new PairedBox(a, false, b), new PairedBox(a, false));

		sdd1.and(sdd2);
	}

	@Test
	public void graphvizOutput() {
		final SDD sdd = example1();
		GraphvizDumper.dump((DecompositionSDD) sdd);
	}

	private VTree vtree1() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final VTree v0 = new LeafNode(b);
		final VTree v1 = new LeafNode(a);
		final VTree v2 = new InternalNode(v0, v1);
		final VTree v3 = new LeafNode(d);
		final VTree v4 = new LeafNode(c);
		final VTree v5 = new InternalNode(v3, v4);
		final VTree v6 = new InternalNode(v2, v5);
		
		return v6;
	}

	private SDD example1() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final InternalNode root = (InternalNode) vtree1();

		final PairedBox n0 = new PairedBox(b, a);
		final PairedBox n1 = new PairedBox(b, false, false);
		final PairedBox n2 = new PairedBox(b, a, false);
		final PairedBox n3 = new PairedBox(d, c);
		final PairedBox n4 = new PairedBox(d, false, false);
		final PairedBox n5 = new PairedBox(AbstractSDD.decomposition(root.getLeft(), n0, n1), true);
		final PairedBox n6 = new PairedBox(AbstractSDD.decomposition(root.getLeft(), n1, n2), c);
		final PairedBox n7 = new PairedBox(b, false, AbstractSDD.decomposition(root.getRight(), n3, n4));

		return AbstractSDD.decomposition(root, n5, n6, n7);
	}

	private SDD example2() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final VTree v0 = new LeafNode(a);
		final VTree v1 = new LeafNode(b);
		final VTree v2 = new LeafNode(c);
		final VTree v3 = new LeafNode(d);
		final VTree v4 = new InternalNode(v2, v3);
		final VTree v5 = new InternalNode(v1, v4);
		final VTree v6 = new InternalNode(v0, v5);

		final PairedBox n0 = new PairedBox(c, d);
		final PairedBox n1 = new PairedBox(c, false, false);
		final PairedBox n2 = new PairedBox(b, true);
		final SDD d0 = AbstractSDD.decomposition(v4, n0, n1);
		final PairedBox n3 = new PairedBox(b, false, d0);
		final PairedBox n4 = new PairedBox(a, AbstractSDD.decomposition(v2, n2, n3));
		final PairedBox n5 = new PairedBox(a, false, d0);

		return AbstractSDD.decomposition(v6, n4, n5);
	}

}
