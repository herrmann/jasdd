package jasdd.tools;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;
import jasdd.bool.transform.DecompositionTransformation;
import jasdd.logic.VariableRegistry;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTreeUtils;

/**
 * Check the size of an SDD from CNF with a random vtree.
 *
 * @author Ricardo Herrmann
 */
public class MinimizationFromRandom {

	public static void main(final String[] args) {
		final VariableRegistry vars = new VariableRegistry();
		final String[] varNames = new String[] { "A", "B", "C", "D", "E", "F" };
		final InternalVTree vtree = VTreeUtils.randomVTree(vars, varNames);
		final DecompositionSDD sdd = example(vars, vtree);
		DecompositionSDD best = sdd;
		for (final DecompositionTransformation transformation : DecompositionTransformation.root) {
			if (transformation.canTransform(sdd)) {
				final DecompositionSDD transformed = (DecompositionSDD) transformation.transform(sdd);
				final int size = transformed.size();
				if (size < best.size()) {
					best = transformed;
				}
				System.out.println(transformed.getVTree().toString(vars) + " - " + size + " (" + transformation.getName() + ")");
			}
		}
		System.out.println("Best size: " + best.size());
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
