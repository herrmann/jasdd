package jasdd.bool.transform;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;

/**
 * Identity transformation.
 *
 * @author Ricardo Herrmann
 */
public class IdentityTransformation implements DecompositionTransformation {

	@Override
	public boolean canTransform(final DecompositionSDD sdd) {
		return true;
	}

	@Override
	public SDD transform(final DecompositionSDD sdd) {
		return sdd;
	}

	@Override
	public String getName() {
		return "none";
	}

}
