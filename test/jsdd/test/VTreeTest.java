package jsdd.test;

import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalRightLinearAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.ValueLeaf;
import jsdd.vtree.VariableLeaf;

import org.junit.Test;

public class VTreeTest {

	@Test
	public void basicSyntax() {
		final VariableLeaf a = new VariableLeaf("A");
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
		new InternalRightLinearAVTree(
			new VariableLeaf("alive(x1,y1)"),
			new InternalRightLinearAVTree(
				new VariableLeaf("alive(x1,y2)"),
				new InternalRightLinearAVTree(
					new VariableLeaf("alive(x2,y1)"),
					new InternalRightLinearAVTree(
						new VariableLeaf("alive(x2,y2)"),
						new ValueLeaf()
					)
				)
			)
		);
	}

	@Test
	public void gameOfLife2() {
		new InternalAVTree(
			new InternalVTree(
				new VariableLeaf("alive(x1,y1)"),
				new VariableLeaf("alive(x1,y2)")
			),
			new InternalRightLinearAVTree(
				new VariableLeaf("alive(x2,y1)"),
				new InternalRightLinearAVTree(
					new VariableLeaf("alive(x2,y2)"),
					new ValueLeaf()
				)
			)
		);
	}

	@Test
	public void gameOfLife3() {
		new InternalAVTree(
			new InternalVTree(
				new VariableLeaf("alive(x1,y1)"),
				new VariableLeaf("alive(x1,y2)")
			),
			new InternalAVTree(
				new InternalVTree(
					new VariableLeaf("alive(x2,y1)"),
					new VariableLeaf("alive(x2,y2)")
				),
				new ValueLeaf()
			)
		);
	}

}
