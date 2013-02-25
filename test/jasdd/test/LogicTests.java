package jasdd.test;

import jasdd.logic.Conjunction;
import jasdd.logic.Constant;
import jasdd.logic.Disjunction;
import jasdd.logic.Formula;
import jasdd.logic.Literal;
import jasdd.logic.Variable;

import java.util.Arrays;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class LogicTests {

	@Test
	public void oppositeLiteralsInDisjunction() {
		Assert.assertEquals(
				new Constant(true),
				new Disjunction(Arrays.asList(new Literal(new Variable(1)),
						new Literal(new Variable(1), false))).trim());
	}

	@Test
	public void oppositeLiteralsInConjunction() {
		Assert.assertEquals(
				new Constant(false),
				new Conjunction(Arrays.asList(new Literal(new Variable(1)),
						new Literal(new Variable(1), false))).trim());
	}

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

	@Test
	public void cnf() {
		final Formula f = Disjunction.from(Arrays.asList(
			Conjunction.from(Arrays.asList(Literal.from(1), Literal.from(2), Literal.from(3))),
			Conjunction.from(Arrays.asList(Literal.from(4), Literal.from(5), Literal.from(6)))));
		final Set<Set<Literal>> cnf = f.toCnf();
		Assert.assertEquals(9, cnf.size());
		for (final Set<Literal> disj : cnf) {
			Assert.assertEquals(2, disj.size());
		}
	}

}
