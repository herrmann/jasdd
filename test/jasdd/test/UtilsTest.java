package jasdd.test;

import jasdd.util.CloneableArrayIterator;
import jasdd.util.SingleElementIterator;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {

	private SingleElementIterator<Integer> createIterator() {
		final SingleElementIterator<Integer> iter = new SingleElementIterator<Integer>(42);
		return iter;
	}

	@Test(expected = NoSuchElementException.class)
	public void usedSingleElementIterator() {
		final SingleElementIterator<Integer> iter = createIterator();
		iter.next();
		iter.next();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void cannotRemoveSingleElementFromIterator() {
		final SingleElementIterator<Integer> iter = createIterator();
		iter.remove();
	}

	@Test
	public void cloneArrayIterator() {
		final CloneableArrayIterator<Integer> iter = CloneableArrayIterator.build(1, 2, 3);
		iter.next();
		final CloneableArrayIterator<Integer> iter2 = iter.clone();
		int i = 0;
		while (iter.hasNext()) {
			iter.next();
			i++;
		}
		while (iter2.hasNext()) {
			iter2.next();
			i++;
		}
		Assert.assertEquals(4, i);
	}

}
