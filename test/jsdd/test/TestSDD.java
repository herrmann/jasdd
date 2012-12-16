package jsdd.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jsdd.AbstractSDD;
import jsdd.ConstantSDD;
import jsdd.DecompositionSDD;
import jsdd.Element;
import jsdd.InternalNode;
import jsdd.LeafNode;
import jsdd.LiteralSDD;
import jsdd.SDD;
import jsdd.VTree;
import jsdd.Variable;
import jsdd.VariableRegistry;
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
		final DecompositionSDD sdd = (DecompositionSDD) exampleDarwiche();
		final VTree vtree = sdd.getVTree();
		Assert.assertTrue(!vtree.isRightLinear());
		Assert.assertEquals("((2, 1), (4, 3))", vtree.toString());
	}

	@Test
	public void correctConstruction2() {
		final DecompositionSDD sdd = (DecompositionSDD) exampleBDD();
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
		final InternalNode vl = (InternalNode) root.getLeft();
		final InternalNode vr = (InternalNode) root.getRight();

		final Element n0 = new Element(b, a);
		final Element n1 = new Element(b, false, false);
		final Element n2 = new Element(b, a, false);
		final Element n3 = new Element(d, c);
		final Element n4 = new Element(d, false, false);
		final Element n5 = new Element(AbstractSDD.decomposition(vl, n0, n1), true);
		final Element n6 = new Element(AbstractSDD.decomposition(vl, n1, n2), c);
		final Element n7 = new Element(b, false, AbstractSDD.decomposition(vr, n3, n4));

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
	public void simpleDecompositionAndSimpleDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalNode vtree = new InternalNode(a, b);

		// A /\ -B
		final SDD sdd1 = AbstractSDD.decomposition(vtree, new Element(a, b, false), new Element(a, false, false));
		
		// -A /\ B
		final SDD sdd2 = AbstractSDD.decomposition(vtree, new Element(a, false, b), new Element(a, false));

		final SDD result = sdd1.and(sdd2);
		Assert.assertEquals("F", result.toString());
	}

	@Test
	public void simpleDecompositionOrSimpleDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalNode vtree = new InternalNode(a, b);

		// A /\ -B
		final SDD sdd1 = AbstractSDD.decomposition(vtree, new Element(a, b, false), new Element(a, false, false));
		
		// -A /\ B
		final SDD sdd2 = AbstractSDD.decomposition(vtree, new Element(a, false, b), new Element(a, false));

		final SDD result = sdd1.or(sdd2);
		Assert.assertEquals("(1) /\\ {-2} \\/ (-1) /\\ {2}", result.toString());
	}

	@Test
	public void graphvizOutput() {
		final SDD sdd = exampleDarwiche();
		GraphvizDumper.dump((DecompositionSDD) sdd);
	}

	@Test
	public void graphvizVTreeOutput() {
		try {
			GraphvizDumper.setOutput(new PrintStream("sdd.dot"));
		} catch (final FileNotFoundException e) {
		}
		GraphvizDumper.dump(bigVTree());
	}

	@Test
	public void simpleCompression() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		
		final VTree va = new LeafNode(a);
		final VTree vb = new LeafNode(b);
		final VTree vc = new LeafNode(c);
		final VTree v1 = new InternalNode(va, vb);
		final InternalNode v0 = new InternalNode(v1, vc);
		
		final SDD aOrC = AbstractSDD.decomposition(v0, new Element(a, true), new Element(a, false, c));
		final SDD bOrC = AbstractSDD.decomposition(v0, new Element(b, true), new Element(b, false, c));
		
		final SDD result = aOrC.or(bOrC);
		Assert.assertEquals("1 \\/ (-1) /\\ {2} \\/ ((-1) /\\ {-2}) /\\ {3}", result.toString());
	}

	@Test
	public void primeLiteralAndDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalNode vtree = new InternalNode(a, b);
		final SDD sdd1 = new LiteralSDD(a);
		final SDD sdd2 = AbstractSDD.decomposition(vtree, new Element(a, b, false), new Element(a, false, false));
		final SDD result = sdd1.and(sdd2);
		Assert.assertEquals("(1) /\\ {-2}", result.toString());
	}

	@Test
	public void subLiteralAndDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalNode vtree = new InternalNode(a, b);
		final SDD sdd1 = new LiteralSDD(b);
		final SDD sdd2 = AbstractSDD.decomposition(vtree, new Element(a, b, false), new Element(a, false, false));
		final SDD result = sdd1.and(sdd2);
		Assert.assertEquals("F", result.toString());
	}

	@Test
	public void normalization() {
		exampleNormalized();
	}

	@Test
	public void trimming() {
		((DecompositionSDD) exampleNormalized()).trimmed();
	}

	@Test
	public void namedVTreeVariables() throws FileNotFoundException {
		final String[] names = new String[] { "C", "PL", "APU", "BPU", "ADR", "BDR", "BO" };
		final VariableRegistry vars = new VariableRegistry();
		final VTree vtree = VTree.buildRightLinear(vars, names);
		GraphvizDumper.dump(vtree, vars, "sdd.gv");
	}

	@Test
	public void rightLinearGameOfLife() throws FileNotFoundException {
		final ArrayList<String> names = new ArrayList<String>();
		for (int x = 1; x <= 2; x++) {
			for (int y = 1; y <= 2; y++) {
				final String name = "alive(x" + x + ",y" + y + ")";
				names.add(name);
			}
		}
		names.add("value");
		final String[] names2 = new String[names.size()];
		names.toArray(names2);
		final VariableRegistry vars = new VariableRegistry();

		final InternalNode vtree = (InternalNode) VTree.buildRightLinear(vars, names2);

		final InternalNode l1 = vtree;
		final InternalNode l2 = (InternalNode) l1.getRight();
		final InternalNode l3 = (InternalNode) l2.getRight();
		final InternalNode l4 = (InternalNode) l3.getRight();
		
		final DecompositionSDD a22 = new DecompositionSDD(l4, new Element(vars.register("alive(x2,y2)"), true), new Element(vars.register("alive(x2,y2)"), false, false));
		final DecompositionSDD a212 = new DecompositionSDD(l3, new Element(vars.register("alive(x2,y1)"), true), new Element(vars.register("alive(x2,y1)"), false, a22));
		final DecompositionSDD a211 = new DecompositionSDD(l3, new Element(vars.register("alive(x2,y1)"), a22), new Element(vars.register("alive(x2,y1)"), false, false));
		final DecompositionSDD a122 = new DecompositionSDD(l2, new Element(vars.register("alive(x1,y2)"), a212), new Element(vars.register("alive(x1,y2)"), false, a211));
		final DecompositionSDD a121 = new DecompositionSDD(l2, new Element(vars.register("alive(x1,y2)"), a211), new Element(vars.register("alive(x1,y2)"), false, false));
		final DecompositionSDD a11 = new DecompositionSDD(l1, new Element(vars.register("alive(x1,y1)"), a122), new Element(vars.register("alive(x1,y1)"), false, a121));

		GraphvizDumper.dump(a11, vars, "sdd.gv");
	}

	@Test
	public void pseudoRightLinearGameOfLife() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		final VTree x1y1 = VTree.register(vars, "alive(x1,y1)");
		final VTree x1y2 = VTree.register(vars, "alive(x1,y2)");
		final InternalNode left = new InternalNode(x1y1, x1y2);
		final InternalNode right = (InternalNode) VTree.buildRightLinear(vars, "alive(x2,y1)", "alive(x2,y2)", "value");
		final InternalNode root = new InternalNode(left,  right);

		final DecompositionSDD a22 = new DecompositionSDD(right.getRight(), new Element(vars.register("alive(x2,y2)"), true), new Element(vars.register("alive(x2,y2)"), false, false));
		final DecompositionSDD a212 = new DecompositionSDD(right, new Element(vars.register("alive(x2,y1)"), true), new Element(vars.register("alive(x2,y1)"), false, a22));
		final DecompositionSDD a211 = new DecompositionSDD(right, new Element(vars.register("alive(x2,y1)"), a22), new Element(vars.register("alive(x2,y1)"), false, false));

		final DecompositionSDD part1 = new DecompositionSDD(left,
			new Element(vars.register("alive(x1,y1)"), vars.register("alive(x1,y2)")),
			new Element(vars.register("alive(x1,y1)"), false, false));
		
		final DecompositionSDD part2 = new DecompositionSDD(left,
				new Element(vars.register("alive(x1,y1)"), vars.register("alive(x1,y2)"), false),
				new Element(vars.register("alive(x1,y1)"), false, vars.register("alive(x1,y2)")));
			
		final DecompositionSDD part3 = new DecompositionSDD(left,
				new Element(vars.register("alive(x1,y1)"), false),
				new Element(vars.register("alive(x1,y1)"), false, vars.register("alive(x1,y2)"), false));

		final DecompositionSDD sdd = new DecompositionSDD(root, new Element(part1, a212), new Element(part2, a211), new Element(part3, false));

		GraphvizDumper.dump(sdd, vars, "sdd.gv");
	}

	@Test
	public void exampleSemiADD() {
		final VariableRegistry vars = new VariableRegistry();
		final Variable c = vars.register("C");
		final Variable pl = vars.register("PL");
		final Variable apu = vars.register("APU");
		final Variable bpu = vars.register("BPU");
		final Variable adr = vars.register("ADR");
		final Variable bdr = vars.register("BDR");
		final Variable bo = vars.register("BO");
		final Variable cprime = vars.register("C'");
		final InternalNode boPart = new InternalNode(bo, cprime);
		final InternalNode bdrPart = new InternalNode(bdr, boPart);
		final InternalNode adrPart = new InternalNode(adr, bdrPart);
		final InternalNode bpuPart = new InternalNode(bpu, adrPart);
		final InternalNode apuPart = new InternalNode(apu, bpuPart);
		final InternalNode plPart = new InternalNode(pl, apuPart);
		final InternalNode cPart = new InternalNode(c, plPart);
		final SDD boNode = new DecompositionSDD(boPart, new Element(bo, true), new Element(bo, false, false));
		final SDD bdrNode = new DecompositionSDD(bdrPart, new Element(bdr, boNode), new Element(bdr, false, false));
		final SDD adrNode = new DecompositionSDD(adrPart, new Element(adr, bdrNode), new Element(adr, false, false));
		final SDD bpuNode = new DecompositionSDD(bpuPart, new Element(bpu, boNode), new Element(bpu, false, adrNode));
		final SDD apuNode = new DecompositionSDD(apuPart, new Element(apu, bpuNode), new Element(apu, false, adrNode));
		final SDD plNode = new DecompositionSDD(plPart, new Element(pl, apuNode), new Element(pl, false, adrNode));
		final SDD cNode = new DecompositionSDD(cPart, new Element(c, true), new Element(c, false, plNode));
		cNode.dump();
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

	private VTree bigVTree() {
		return new InternalNode(1, new InternalNode(2, new InternalNode(new InternalNode(3, 4), new InternalNode(5, 6))));
	}

	private SDD exampleDarwiche() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final InternalNode root = (InternalNode) vtree1();
		final InternalNode vl = (InternalNode) root.getLeft();
		final InternalNode vr = (InternalNode) root.getRight();

		final Element n0 = new Element(b, a);
		final Element n1 = new Element(b, false, false);
		final Element n2 = new Element(b, a, false);
		final Element n3 = new Element(d, c);
		final Element n4 = new Element(d, false, false);
		final Element n5 = new Element(AbstractSDD.decomposition(vl, n0, n1), true);
		final Element n6 = new Element(AbstractSDD.decomposition(vl, n1, n2), c);
		final Element n7 = new Element(b, false, AbstractSDD.decomposition(vr, n3, n4));

		return AbstractSDD.decomposition(root, n5, n6, n7);
	}

	private SDD exampleNormalized() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final InternalNode root = (InternalNode) vtree1();
		final InternalNode vl = (InternalNode) root.getLeft();
		final InternalNode vr = (InternalNode) root.getRight();

		final Element n0 = new Element(b, a);
		final Element n1 = new Element(b, false, false);
		final Element n2 = new Element(b, a, false);
		final Element n3 = new Element(true, c);
		final Element n4 = new Element(b, false, true);
		final Element n5 = new Element(b, false);
		final Element n6 = new Element(d, c);
		final Element n7 = new Element(d, false, false);

		final Element n8 = new Element(AbstractSDD.decomposition(vl, n0, n1), true);
		final Element n9 = new Element(AbstractSDD.decomposition(vl, n1, n2), AbstractSDD.decomposition(vr, n3));
		final Element n10 = new Element(AbstractSDD.decomposition(vl, n4, n5), AbstractSDD.decomposition(vr, n6, n7));

		return AbstractSDD.decomposition(root, n8, n9, n10);
	}

	private SDD exampleBDD() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final LeafNode v0 = new LeafNode(a);
		final LeafNode v1 = new LeafNode(b);
		final LeafNode v2 = new LeafNode(c);
		final LeafNode v3 = new LeafNode(d);
		final InternalNode v4 = new InternalNode(v2, v3);
		final InternalNode v5 = new InternalNode(v1, v4);
		final InternalNode v6 = new InternalNode(v0, v5);

		final Element n0 = new Element(c, d);
		final Element n1 = new Element(c, false, false);
		final Element n2 = new Element(b, true);
		final SDD d0 = AbstractSDD.decomposition(v4, n0, n1);
		final Element n3 = new Element(b, false, d0);
		final Element n4 = new Element(a, AbstractSDD.decomposition(v5, n2, n3));
		final Element n5 = new Element(a, false, d0);

		return AbstractSDD.decomposition(v6, n4, n5);
	}

}
