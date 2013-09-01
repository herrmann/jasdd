package jasdd.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Reusable simple methods that are not present in the standard libraries.
 * 
 * @author Ricardo Herrmann
 */
public class Utils {

	/**
	 * Shuffles an array in-place.
	 * 
	 * @param array
	 *            the array to be shuffled
	 * @return the same array with its elements shuffled
	 */
	@SafeVarargs
	public static <T> T[] shuffleArray(final T... array) {
		return shuffleArray(new Random(), array);
	}

	/**
	 * Shuffles an array in-place.
	 * 
	 * @param rng
	 *            the randomness source
	 * @param array
	 *            the array to be shuffled
	 * @return the same array with its elements shuffled
	 */
	@SafeVarargs
	public static <T> T[] shuffleArray(final Random rng, final T... array) {
		final int length = array.length - 1;
		for (int i = 0; i < length; i++) {
			final int pos = i + rng.nextInt(length - i + 1);
			final T tmp = array[i];
			array[i] = array[pos];
			array[pos] = tmp;
		}
		return array;
	}

	public interface MergeFunction<T> {
		T apply(T first, T second);
	}

	/**
	 * Returns a new list with the same elements, in the same order, but merging
	 * two adjacent elements at a random position merged.
	 * 
	 * @param rng
	 *            the randomness source
	 * @param merge
	 *            the pairwise element merging function
	 * @param elems
	 *            the list of elements
	 * @return the new list containing the merged element and the rest of the
	 *         original ones untouched, in the same order
	 */
	public static <T> List<T> randomlyMergeOne(final Random rng, final MergeFunction<T> merge, final List<T> elems) {
		final int size = elems.size();
		if (size > 1) {
			final List<T> newElems = new ArrayList<T>(size - 1);
			final int pos = size > 2 ? rng.nextInt(size - 2) : 0;
			final Iterator<T> iter = elems.iterator();
			int i = 0;
			while (iter.hasNext()) {
				T elem = iter.next();
				if (pos == i++) {
					elem = merge.apply(elem, iter.next());
				}
				newElems.add(elem);
			}
			return newElems;
		} else {
			return elems;
		}
	}

}