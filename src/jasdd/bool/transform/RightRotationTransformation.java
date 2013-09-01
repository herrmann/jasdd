package jasdd.bool.transform;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;

/**
 * Right rotation as transformation.
 *
 * @author Ricardo Herrmann
 */
public class RightRotationTransformation implements DecompositionTransformation {

	@Override
	public boolean canTransform(final DecompositionSDD sdd) {
		return sdd.canRotateRight();
	}

	@Override
	public SDD transform(final DecompositionSDD sdd) {
		return sdd.rotateRight();
	}

	@Override
	public String getName() {
		return "rotate right";
	}

}