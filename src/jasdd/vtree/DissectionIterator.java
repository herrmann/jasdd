package jasdd.vtree;

import jasdd.util.PartitioningIterator;

/**
 * Lazily generates tree dissections.
 * 
 * @author Ricardo Herrmann
 */
public class DissectionIterator extends PartitioningIterator<Tree> {

	public DissectionIterator(final Tree[] leaves) {
		super(leaves, new PartitioningIterator.Combiner<Tree>() {
			@Override
			public Tree combine(final Tree left, final Tree right, final Tree[] leaves, final int begin, final int end) {
				if (end < leaves.length) {
					return new InternalVTree((VTree) left, (VTree) right);
				} else {
					return new InternalAVTree((VTree) left, (AVTree) right);
				}
			}
		});
	}

}
