package jasdd.bool.transform;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;

/**
 * Left rotation as transformation..
 *
 * @author Ricardo Herrmann
 */
public class LeftRotationTransformation implements DecompositionTransformation {

	@Override
	public boolean canTransform(final DecompositionSDD sdd) {
		return sdd.canRotateLeft();
	}

	@Override
	public SDD transform(final DecompositionSDD sdd) {
		return sdd.rotateLeft();
	}

	@Override
	public String getName() {
		return "rotate left";
	}

}