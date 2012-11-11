package jsdd;

public class Test {

	public static void main(final String[] args) {
		System.out.println(example1().toString());
		System.out.println(example2().toString());
	}

	private static SDD example1() {
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

		System.out.println("Right-linear: " + v6.isRightLinear() + " " + v6);

		final PairedBox n0 = new PairedBox(b, a);
		final PairedBox n1 = new PairedBox(b, false, false);
		final PairedBox n2 = new PairedBox(b, a, false);
		final PairedBox n3 = new PairedBox(d, c);
		final PairedBox n4 = new PairedBox(d, false, false);
		final PairedBox n5 = new PairedBox(SDD.decomposition(v2, n0, n1), true);
		final PairedBox n6 = new PairedBox(SDD.decomposition(v2, n1, n2), c);
		final PairedBox n7 = new PairedBox(b, false, SDD.decomposition(v5, n3, n4));

		return SDD.decomposition(v6, n5, n6, n7);
	}

	private static SDD example2() {
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

		System.out.println("Right-linear: " + v6.isRightLinear() + " " + v6);

		final PairedBox n0 = new PairedBox(c, d);
		final PairedBox n1 = new PairedBox(c, false, false);
		final PairedBox n2 = new PairedBox(b, true);
		final SDD d0 = SDD.decomposition(v4, n0, n1);
		final PairedBox n3 = new PairedBox(b, false, d0);
		final PairedBox n4 = new PairedBox(a, SDD.decomposition(v2, n2, n3));
		final PairedBox n5 = new PairedBox(a, false, d0);

		return SDD.decomposition(v6, n4, n5);
	}

}
