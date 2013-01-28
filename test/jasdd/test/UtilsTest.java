package jasdd.test;

import jasdd.util.SingleElementIterator;

import java.util.NoSuchElementException;

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

}
