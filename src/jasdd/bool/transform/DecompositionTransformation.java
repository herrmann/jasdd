package jasdd.bool.transform;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;

/**
 * Possible decomposition transformations.
 *
 * @author Ricardo Herrmann
 */
public interface DecompositionTransformation {

	boolean canTransform(DecompositionSDD sdd);

	SDD transform(DecompositionSDD sdd);

	String getName();

	final DecompositionTransformation[] root = new DecompositionTransformation[] {
		new IdentityTransformation(),
		new SwapTransformation(),
		new LeftRotationTransformation(),
		new RightRotationTransformation()
	};

}