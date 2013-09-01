package jasdd.bool.transform;

import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;

/**
 * Swap as transformation.
 *
 * @author Ricardo Herrmann
 */
public class SwapTransformation implements DecompositionTransformation {

	@Override
	public boolean canTransform(final DecompositionSDD sdd) {
		return sdd.canSwap();
	}

	@Override
	public SDD transform(final DecompositionSDD sdd) {
		return sdd.swap();
	}

	@Override
	public String getName() {
		return "swap";
	}

}