package jasdd.tools;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;
import jasdd.bool.transform.DecompositionTransformation;
import jasdd.logic.VariableRegistry;
import jasdd.util.Utils;
import jasdd.util.Utils.MergeFunction;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;
import jasdd.vtree.VariableLeaf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Check the size of an SDD from CNF with a random vtree.
 *
 * @author Ricardo Herrmann
 */
public class MinimizationFromRandom {

	public static void main(final String[] args) {
		final VariableRegistry vars = new VariableRegistry();
		final String[] varNames = new String[] { "A", "B", "C", "D", "E", "F" };
		final InternalVTree vtree = randomVTree(vars, varNames);
		final DecompositionSDD sdd = example(vars, vtree);
		int best = sdd.size();
		for (final DecompositionTransformation transformation : DecompositionTransformation.root) {
			if (transformation.canTransform(sdd)) {
				final DecompositionSDD transformed = (DecompositionSDD) transformation.transform(sdd);
				final int size = transformed.size();
				if (size < best) {
					best = size;
				}
				System.out.println(transformed.getVTree().toString(vars) + " - " + size + " (" + transformation.getName() + ")");
			}
		}
		System.out.println("Best size: " + best);
	}

	private static final MergeFunction<VTree> createInternal = new Utils.MergeFunction<VTree>() {
		@Override
		public VTree apply(final VTree first, final VTree second) {
			return new InternalVTree(first, second);
		}
	};

	private static InternalVTree randomVTree(final VariableRegistry vars, final String... varNames) {
		final Random rng = new Random();
		Utils.shuffleArray(rng, varNames);
		List<VTree> vtrees = new ArrayList<VTree>(varNames.length);
		for (final String varName : varNames) {
			final VTree vtree = new VariableLeaf(vars.register(varName));
			vtrees.add(vtree);
		}
		for (int i = 0; i < varNames.length - 1; i++) {
			vtrees = Utils.randomlyMergeOne(rng, createInternal, vtrees);
		}
		return (InternalVTree) vtrees.iterator().next();
	}

	private static DecompositionSDD example(final VariableRegistry vars, final InternalVTree vtree) {
		final SDD sddA = DecompositionSDD.buildNormalized(vtree, vars.register("A"));
		final SDD sddNotA = DecompositionSDD.buildNormalized(vtree, vars.register("A"), false);
		final SDD sddB = DecompositionSDD.buildNormalized(vtree, vars.register("B"));
		final SDD sddC = DecompositionSDD.buildNormalized(vtree, vars.register("C"));
		final SDD sddNotC = DecompositionSDD.buildNormalized(vtree, vars.register("C"), false);
		final SDD sddD = DecompositionSDD.buildNormalized(vtree, vars.register("D"));
		final SDD sddNotE = DecompositionSDD.buildNormalized(vtree, vars.register("E"), false);
		final SDD sddF = DecompositionSDD.buildNormalized(vtree, vars.register("F"));

		final SDD sdd1 = sddA.or(sddB).or(sddNotC);
		final SDD sdd2 = sddNotA.or(sddC).or(sddD);
		final SDD sdd3 = sddA.or(sddB).or(sddNotE);
		final SDD sdd4 = sddB.or(sddF);

		final DecompositionSDD sdd = (DecompositionSDD) sdd1.and(sdd2).and(sdd3).and(sdd4);
		return sdd;
	}

}
