package jasdd.test;

import jasdd.logic.Conjunction;
import jasdd.logic.Constant;
import jasdd.logic.Disjunction;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.Variable;

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class LogicTests {

	@Test
	public void falseEliminationFromDisjunctionTrimming() {
		final Formula a = new Literal(new Variable(1));
		final Formula b = new Literal(new Variable(2));
		final Formula f = new Constant(false);
		final Formula formula = new Disjunction(Arrays.asList(f, a, b));
		final Formula trimmed = formula.trim();
		final Formula expected = new Disjunction(Arrays.asList(a, b));
		Assert.assertEquals(expected, trimmed);
	}

	@Test
	public void trueEliminationFromConjunctionTrimming() {
		final Formula a = new Literal(new Variable(1));
		final Formula b = new Literal(new Variable(2));
		final Formula f = new Constant(true);
		final Formula formula = new Conjunction(Arrays.asList(f, a, b));
		final Formula trimmed = formula.trim();
		final Formula expected = new Conjunction(Arrays.asList(a, b));
		Assert.assertEquals(expected, trimmed);
	}

}
