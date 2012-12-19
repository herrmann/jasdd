package jsdd.test;

import jsdd.VariableRegistry;
import jsdd.vtree.InternalAVTree;
import jsdd.vtree.InternalRightLinearAVTree;
import jsdd.vtree.InternalVTree;
import jsdd.vtree.ValueLeaf;
import jsdd.vtree.VariableLeaf;

import org.junit.Test;

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
		new InternalRightLinearAVTree(
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
	}

	@Test
	public void gameOfLife2() {
		final VariableRegistry vars = new VariableRegistry();
		new InternalAVTree(
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

}
