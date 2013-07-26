package jasdd.test;

import jasdd.util.CloneableArrayIterator;
import jasdd.util.ExpandingIterator;
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

	@Test
	public void expandIterator() {
		final ExpandingIterator<Integer> iter = ExpandingIterator.build(1, 2, 3);
		int i = 0;
		while (iter.hasNext()) {
			iter.next();
			i++;
		}
		iter.add(4, 5);
		iter.next();
		while (iter.hasNext()) {
			iter.next();
			i++;
		}
		Assert.assertEquals(4, i);
	}

	@Test
	public void cloneExpandingIterator() {
		final ExpandingIterator<Integer> iter = ExpandingIterator.build(1, 2, 3);
		iter.next();
		final ExpandingIterator<Integer> iter2 = iter.clone();
		int i = 0;
		while (iter2.hasNext()) {
			iter2.next();
			i++;
		}
		Assert.assertEquals(2, i);
	}

}
