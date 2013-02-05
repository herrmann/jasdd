package jasdd.test;

import jasdd.bool.AbstractSDD;
import jasdd.bool.ConstantSDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.Element;
import jasdd.bool.LiteralSDD;
import jasdd.bool.SDD;
import jasdd.bool.SDDFactory;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;
import jasdd.vtree.VTreeUtils;
import jasdd.vtree.VariableLeaf;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import org.junit.Assert;
import org.junit.Test;

/**
 * Integration tests for SDD construction and manipulation.
 *  
 * @author Ricardo Herrmann
 */
public class TestSDD {

	private SDDFactory factory = SDDFactory.getInstance();

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
		Assert.assertEquals("((2,1),(4,3))", vtree.toString());
	}

	@Test
	public void correctConstruction2() {
		final DecompositionSDD sdd = (DecompositionSDD) exampleBDD();
		final VTree vtree = sdd.getVTree();
		Assert.assertTrue(vtree.isRightLinear());
		Assert.assertEquals("(1,(2,(3,4)))", vtree.toString());
	}

	@Test
	public void booleanPairedBoxSerialization() {
		Assert.assertEquals("[(T /\\ T)]", new ConstantSDD(true).expansion().toString());
	}

	@Test
	public void singleLiteralPairedBoxSerialization() {
		Assert.assertEquals("[(1 /\\ T), (-1 /\\ F)]", factory.createLiteral(1).expansion().toString());
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

		final InternalVTree root = (InternalVTree) vtree1();
		final InternalVTree vl = (InternalVTree) root.getLeft();
		final InternalVTree vr = (InternalVTree) root.getRight();

		final Element n0 = factory.createElement(b, a);
		final Element n1 = factory.createElement(b, false, false);
		final Element n2 = factory.createElement(b, a, false);
		final Element n3 = factory.createElement(d, c);
		final Element n4 = factory.createElement(d, false, false);
		final Element n5 = factory.createElement(AbstractSDD.decomposition(vl, n0, n1), true);
		final Element n6 = factory.createElement(AbstractSDD.decomposition(vl, n1, n2), c);
		final Element n7 = factory.createElement(b, false, AbstractSDD.decomposition(vr, n3, n4));

		AbstractSDD.decomposition(root, n5, n6, n7);
	}

	@Test
	public void simpleDecompositionAndSimpleDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalVTree vtree = new InternalVTree(a, b);

		// A /\ -B
		final SDD sdd1 = AbstractSDD.decomposition(vtree,
			factory.createElement(a, b, false),
			factory.createElement(a, false, false));

		// -A /\ B
		final SDD sdd2 = AbstractSDD.decomposition(vtree,
			factory.createElement(a, false, b),
			factory.createElement(a, false));

		final SDD result = sdd1.and(sdd2);
		Assert.assertEquals("F", result.toString());
	}

	@Test
	public void simpleDecompositionOrSimpleDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalVTree vtree = new InternalVTree(a, b);

		// A /\ -B
		final SDD sdd1 = AbstractSDD.decomposition(vtree,
			factory.createElement(a, b, false),
			factory.createElement(a, false, false));
		
		// -A /\ B
		final SDD sdd2 = AbstractSDD.decomposition(vtree,
			factory.createElement(a, false, b),
			factory.createElement(a, false));

		final SDD result = sdd1.or(sdd2);
		Assert.assertEquals("[(1,2), ((1 /\\ -2) \\/ (-1 /\\ 2))]", result.toString());
	}

	@Test
	public void graphvizOutput() throws FileNotFoundException {
		final SDD sdd = exampleDarwiche();
		GraphvizDumper.setOutput("temp.dot");
		GraphvizDumper.dump((DecompositionSDD) sdd);
	}

	@Test
	public void graphvizVTreeOutput() {
		try {
			GraphvizDumper.setOutput("sdd.dot");
		} catch (final FileNotFoundException e) {
		}
		GraphvizDumper.dump(bigVTree());
	}

	@Test
	public void simpleCompression() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		
		final VTree va = new VariableLeaf(a);
		final VTree vb = new VariableLeaf(b);
		final VTree vc = new VariableLeaf(c);
		final VTree v1 = new InternalVTree(va, vb);
		final InternalVTree v0 = new InternalVTree(v1, vc);
		
		final SDD aOrC = AbstractSDD.decomposition(v0,
			factory.createElement(a, true),
			factory.createElement(a, false, c));

		final SDD bOrC = AbstractSDD.decomposition(v0,
			factory.createElement(b, true),
			factory.createElement(b, false, c));

		final SDD result = aOrC.or(bOrC);
		Assert.assertEquals("[((1,2),3), (([(1,2), ((1 /\\ T) \\/ (-1 /\\ 2))] /\\ T) \\/ ([(1,2), ((-1 /\\ -2) \\/ (1 /\\ F))] /\\ 3))]", result.toString());
	}

	@Test
	public void primeLiteralAndDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalVTree vtree = new InternalVTree(a, b);
		final SDD sdd1 = factory.createLiteral(a);
		final SDD sdd2 = AbstractSDD.decomposition(vtree, factory.createElement(a, b, false), factory.createElement(a, false, false));
		final SDD result = sdd1.and(sdd2);
		Assert.assertEquals("[(1,2), ((1 /\\ -2) \\/ (-1 /\\ F))]", result.toString());
	}

	@Test
	public void subLiteralAndDecomposition() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final InternalVTree vtree = new InternalVTree(a, b);
		final SDD sdd1 = factory.createLiteral(b);
		final SDD sdd2 = AbstractSDD.decomposition(vtree, factory.createElement(a, b, false), factory.createElement(a, false, false));
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
		final VTree vtree = VTreeUtils.buildRightLinear(vars, names);
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

		final InternalVTree vtree = (InternalVTree) VTreeUtils.buildRightLinear(vars, names2);

		final InternalVTree l1 = vtree;
		final InternalVTree l2 = (InternalVTree) l1.getRight();
		final InternalVTree l3 = (InternalVTree) l2.getRight();
		final InternalVTree l4 = (InternalVTree) l3.getRight();
		
		final DecompositionSDD a22 = factory.createDecomposition(l4, factory.createElement(vars.register("alive(x2,y2)"), true), factory.createElement(vars.register("alive(x2,y2)"), false, false));
		final DecompositionSDD a212 = factory.createDecomposition(l3, factory.createElement(vars.register("alive(x2,y1)"), true), factory.createElement(vars.register("alive(x2,y1)"), false, a22));
		final DecompositionSDD a211 = factory.createDecomposition(l3, factory.createElement(vars.register("alive(x2,y1)"), a22), factory.createElement(vars.register("alive(x2,y1)"), false, false));
		final DecompositionSDD a122 = factory.createDecomposition(l2, factory.createElement(vars.register("alive(x1,y2)"), a212), factory.createElement(vars.register("alive(x1,y2)"), false, a211));
		final DecompositionSDD a121 = factory.createDecomposition(l2, factory.createElement(vars.register("alive(x1,y2)"), a211), factory.createElement(vars.register("alive(x1,y2)"), false, false));
		final DecompositionSDD a11 = factory.createDecomposition(l1, factory.createElement(vars.register("alive(x1,y1)"), a122), factory.createElement(vars.register("alive(x1,y1)"), false, a121));

		GraphvizDumper.dump(a11, vars, "sdd.gv");
	}

	@Test
	public void pseudoRightLinearGameOfLife() throws FileNotFoundException {
		final VariableRegistry vars = new VariableRegistry();
		final VTree x1y1 = VTreeUtils.register(vars, "alive(x1,y1)");
		final VTree x1y2 = VTreeUtils.register(vars, "alive(x1,y2)");
		final InternalVTree left = new InternalVTree(x1y1, x1y2);
		final InternalVTree right = (InternalVTree) VTreeUtils.buildRightLinear(vars, "alive(x2,y1)", "alive(x2,y2)", "value");
		final InternalVTree root = new InternalVTree(left,  right);

		final DecompositionSDD a22 = factory.createDecomposition((InternalVTree) right.getRight(), factory.createElement(vars.register("alive(x2,y2)"), true), factory.createElement(vars.register("alive(x2,y2)"), false, false));
		final DecompositionSDD a212 = factory.createDecomposition(right, factory.createElement(vars.register("alive(x2,y1)"), true), factory.createElement(vars.register("alive(x2,y1)"), false, a22));
		final DecompositionSDD a211 = factory.createDecomposition(right, factory.createElement(vars.register("alive(x2,y1)"), a22), factory.createElement(vars.register("alive(x2,y1)"), false, false));

		final DecompositionSDD part1 = factory.createDecomposition(left,
			factory.createElement(vars.register("alive(x1,y1)"), vars.register("alive(x1,y2)")),
			factory.createElement(vars.register("alive(x1,y1)"), false, false));
		
		final DecompositionSDD part2 = factory.createDecomposition(left,
			factory.createElement(vars.register("alive(x1,y1)"), vars.register("alive(x1,y2)"), false),
			factory.createElement(vars.register("alive(x1,y1)"), false, vars.register("alive(x1,y2)")));
			
		final DecompositionSDD part3 = factory.createDecomposition(left,
			factory.createElement(vars.register("alive(x1,y1)"), false),
			factory.createElement(vars.register("alive(x1,y1)"), false, vars.register("alive(x1,y2)"), false));

		final DecompositionSDD sdd = factory.createDecomposition(root,
			factory.createElement(part1, a212),
			factory.createElement(part2, a211),
			factory.createElement(part3, false));

		Assert.assertEquals(15, sdd.size());

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
		final InternalVTree boPart = new InternalVTree(bo, cprime);
		final InternalVTree bdrPart = new InternalVTree(bdr, boPart);
		final InternalVTree adrPart = new InternalVTree(adr, bdrPart);
		final InternalVTree bpuPart = new InternalVTree(bpu, adrPart);
		final InternalVTree apuPart = new InternalVTree(apu, bpuPart);
		final InternalVTree plPart = new InternalVTree(pl, apuPart);
		final InternalVTree cPart = new InternalVTree(c, plPart);
		final SDD boNode = factory.createDecomposition(boPart, factory.createElement(bo, true), factory.createElement(bo, false, false));
		final SDD bdrNode = factory.createDecomposition(bdrPart, factory.createElement(bdr, boNode), factory.createElement(bdr, false, false));
		final SDD adrNode = factory.createDecomposition(adrPart, factory.createElement(adr, bdrNode), factory.createElement(adr, false, false));
		final SDD bpuNode = factory.createDecomposition(bpuPart, factory.createElement(bpu, boNode), factory.createElement(bpu, false, adrNode));
		final SDD apuNode = factory.createDecomposition(apuPart, factory.createElement(apu, bpuNode), factory.createElement(apu, false, adrNode));
		final SDD plNode = factory.createDecomposition(plPart, factory.createElement(pl, apuNode), factory.createElement(pl, false, adrNode));
		final SDD cNode = factory.createDecomposition(cPart, factory.createElement(c, true), factory.createElement(c, false, plNode));
		cNode.dump();
	}

	private InternalVTree example1(final VariableRegistry vars) {
		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Variable d = vars.register("D");
		return new InternalVTree(new InternalVTree(b, a), new InternalVTree(d, c));
	}

	@Test
	public void normalizedConstructionA() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sdd = DecompositionSDD.buildNormalized(vtree, vars.register("A"));
		Assert.assertEquals(4, sdd.size());
		Assert.assertEquals(vtree, ((DecompositionSDD) sdd).getVTree());
		Assert.assertEquals(2, sdd.expansion().size());
		final Element elem = sdd.expansion().iterator().next();
		Assert.assertEquals(new ConstantSDD(true), elem.getSub());
		Assert.assertEquals(vtree.getLeft(), ((DecompositionSDD) elem.getPrime()).getVTree());
		Assert.assertEquals(1, elem.getPrime().expansion().size());
	}

	@Test
	public void normalizedConstructionB() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sdd = DecompositionSDD.buildNormalized(vtree, vars.register("B"));
		Assert.assertEquals(6, sdd.size());
		Assert.assertEquals(vtree, ((DecompositionSDD) sdd).getVTree());
		Assert.assertEquals(2, sdd.expansion().size());
		final Element elem = sdd.expansion().iterator().next();
		Assert.assertEquals(new ConstantSDD(true), elem.getSub());
		Assert.assertEquals(vtree.getLeft(), ((DecompositionSDD) elem.getPrime()).getVTree());
		Assert.assertEquals(2, elem.getPrime().expansion().size());
	}

	@Test
	public void normalizedConstructionC() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sdd = DecompositionSDD.buildNormalized(vtree, vars.register("C"));
		Assert.assertEquals(2, sdd.size());
		Assert.assertEquals(vtree, ((DecompositionSDD) sdd).getVTree());
		Assert.assertEquals(1, sdd.expansion().size());
		final Element elem = sdd.expansion().iterator().next();
		Assert.assertEquals(new ConstantSDD(true), elem.getPrime());
		Assert.assertEquals(vtree.getRight(), ((DecompositionSDD) elem.getSub()).getVTree());
		Assert.assertEquals(1, elem.getSub().expansion().size());
	}

	@Test
	public void normalizedConstructionD() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sdd = DecompositionSDD.buildNormalized(vtree, vars.register("D"));
		Assert.assertEquals(3, sdd.size());
		Assert.assertEquals(vtree, ((DecompositionSDD) sdd).getVTree());
		Assert.assertEquals(1, sdd.expansion().size());
		final Element elem = sdd.expansion().iterator().next();
		Assert.assertEquals(new ConstantSDD(true), elem.getPrime());
		Assert.assertEquals(vtree.getRight(), ((DecompositionSDD) elem.getSub()).getVTree());
		Assert.assertEquals(2, elem.getSub().expansion().size());
	}

	@Test
	public void normalizedConjunctionInSubVTree() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sddA = DecompositionSDD.buildNormalized(vtree, vars.register("A"));
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, vars.register("B"));
		final SDD sdd = sddA.and(sddB);
		Assert.assertEquals(6, sdd.size());
	}

	@Test
	public void normalizedDisjunctionInSubVTree() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sddA = DecompositionSDD.buildNormalized(vtree, vars.register("A"));
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, vars.register("B"));
		final SDD sdd = sddA.or(sddB);
		Assert.assertEquals(6, sdd.size());
	}

	@Test
	public void normalizedConstructionFromDnf2() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = example1(vars);
		final SDD sddA = DecompositionSDD.buildNormalized(vtree, vars.register("A"));
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, vars.register("B"));
		final SDD sddC = DecompositionSDD.buildNormalized(vtree, vars.register("C"));
		final SDD sddD = DecompositionSDD.buildNormalized(vtree, vars.register("D"));
		final SDD sdd1 = sddA.and(sddB);
		final SDD sdd2 = sddB.and(sddC);
		final SDD sdd3 = sddC.and(sddD);
		final SDD sdd = sdd1.or(sdd2).or(sdd3);
		try {
			GraphvizDumper.dump((DecompositionSDD) sdd, vars, "dnf2.dot");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(11, sdd.size());
	}

	@Test
	public void normalizedConstructionFromDnf3() {
		final VariableRegistry vars = new VariableRegistry();
		final InternalVTree vtree = (InternalVTree) VTreeUtils.buildRightLinear(vars, "A", "B", "C", "D");
		final SDD sddA = DecompositionSDD.buildNormalized(vtree, vars.register("A"));
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, vars.register("B"));
		final SDD sddC = DecompositionSDD.buildNormalized(vtree, vars.register("C"));
		final SDD sddD = DecompositionSDD.buildNormalized(vtree, vars.register("D"));
		final SDD sdd1 = sddA.and(sddB);
		final SDD sdd2 = sddC.and(sddD);
		final SDD sdd = sdd1.or(sdd2);
		try {
			GraphvizDumper.dump((DecompositionSDD) sdd, vars, "dnf3.dot");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(7, sdd.size());
	}

	@Test
	public void normalizedConstructionFromCnf4() {
		final VariableRegistry vars = new VariableRegistry();

		final Variable a = vars.register("A");
		final Variable b = vars.register("B");
		final Variable c = vars.register("C");
		final Variable d = vars.register("D");
		final Variable e = vars.register("E");
		final Variable f = vars.register("F");

		final InternalVTree vtree = new InternalVTree(a, new InternalVTree(b, new InternalVTree(new InternalVTree(c, d), new InternalVTree(e, f))));

		final SDD sddA = DecompositionSDD.buildNormalized(vtree, a);
		final SDD sddNotA = DecompositionSDD.buildNormalized(vtree, a, false);
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, b);
		final SDD sddC = DecompositionSDD.buildNormalized(vtree, c);
		final SDD sddNotC = DecompositionSDD.buildNormalized(vtree, c, false);
		final SDD sddD = DecompositionSDD.buildNormalized(vtree, d);
		final SDD sddNotE = DecompositionSDD.buildNormalized(vtree, e, false);
		final SDD sddF = DecompositionSDD.buildNormalized(vtree, f);
		
		final SDD sdd1 = sddA.or(sddB).or(sddNotC);
		final SDD sdd2 = sddNotA.or(sddC).or(sddD);
		final SDD sdd3 = sddA.or(sddB).or(sddNotE);
		final SDD sdd4 = sddB.or(sddF);
		
		final SDD sdd = sdd1.and(sdd2).and(sdd3).and(sdd4);
		
		try {
			GraphvizDumper.dump((DecompositionSDD) sdd, vars, "cnf4.dot");
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	private VTree vtree1() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final VTree v0 = new VariableLeaf(b);
		final VTree v1 = new VariableLeaf(a);
		final VTree v2 = new InternalVTree(v0, v1);
		final VTree v3 = new VariableLeaf(d);
		final VTree v4 = new VariableLeaf(c);
		final VTree v5 = new InternalVTree(v3, v4);
		final VTree v6 = new InternalVTree(v2, v5);
		
		return v6;
	}

	private VTree bigVTree() {
		return new InternalVTree(1, new InternalVTree(2, new InternalVTree(new InternalVTree(3, 4), new InternalVTree(5, 6))));
	}

	private SDD exampleDarwiche() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final InternalVTree root = (InternalVTree) vtree1();
		final InternalVTree vl = (InternalVTree) root.getLeft();
		final InternalVTree vr = (InternalVTree) root.getRight();

		final Element n0 = factory.createElement(b, a);
		final Element n1 = factory.createElement(b, false, false);
		final Element n2 = factory.createElement(b, a, false);
		final Element n3 = factory.createElement(d, c);
		final Element n4 = factory.createElement(d, false, false);
		final Element n5 = factory.createElement(AbstractSDD.decomposition(vl, n0, n1), true);
		final Element n6 = factory.createElement(AbstractSDD.decomposition(vl, n1, n2), c);
		final Element n7 = factory.createElement(b, false, AbstractSDD.decomposition(vr, n3, n4));

		return AbstractSDD.decomposition(root, n5, n6, n7);
	}

	private SDD exampleNormalized() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final InternalVTree root = (InternalVTree) vtree1();
		final InternalVTree vl = (InternalVTree) root.getLeft();
		final InternalVTree vr = (InternalVTree) root.getRight();

		final Element n0 = factory.createElement(b, a);
		final Element n1 = factory.createElement(b, false, false);
		final Element n2 = factory.createElement(b, a, false);
		final Element n3 = factory.createElement(true, c);
		final Element n4 = factory.createElement(b, false, true);
		final Element n5 = factory.createElement(b, false);
		final Element n6 = factory.createElement(d, c);
		final Element n7 = factory.createElement(d, false, false);

		final Element n8 = factory.createElement(AbstractSDD.decomposition(vl, n0, n1), true);
		final Element n9 = factory.createElement(AbstractSDD.decomposition(vl, n1, n2), AbstractSDD.decomposition(vr, n3));
		final Element n10 = factory.createElement(AbstractSDD.decomposition(vl, n4, n5), AbstractSDD.decomposition(vr, n6, n7));

		return AbstractSDD.decomposition(root, n8, n9, n10);
	}

	private SDD exampleBDD() {
		final Variable a = new Variable(1);
		final Variable b = new Variable(2);
		final Variable c = new Variable(3);
		final Variable d = new Variable(4);

		final VariableLeaf v0 = new VariableLeaf(a);
		final VariableLeaf v1 = new VariableLeaf(b);
		final VariableLeaf v2 = new VariableLeaf(c);
		final VariableLeaf v3 = new VariableLeaf(d);
		final InternalVTree v4 = new InternalVTree(v2, v3);
		final InternalVTree v5 = new InternalVTree(v1, v4);
		final InternalVTree v6 = new InternalVTree(v0, v5);

		final Element n0 = factory.createElement(c, d);
		final Element n1 = factory.createElement(c, false, false);
		final Element n2 = factory.createElement(b, true);
		final SDD d0 = AbstractSDD.decomposition(v4, n0, n1);
		final Element n3 = factory.createElement(b, false, d0);
		final Element n4 = factory.createElement(a, AbstractSDD.decomposition(v5, n2, n3));
		final Element n5 = factory.createElement(a, false, d0);

		return AbstractSDD.decomposition(v6, n4, n5);
	}

	@Test
	public void trimTrue() {
		final DecompositionSDD sdd = factory.createDecomposition((InternalVTree) vtree1(), factory.createElement(true, true));
		Assert.assertEquals(new ConstantSDD(true), sdd.trimmed());
	}

}
