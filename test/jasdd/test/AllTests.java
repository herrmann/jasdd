package jasdd.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Run all the tests.
 *
 * @author Ricardo Herrmann
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({UtilsTest.class, LogicTests.class, VTreeTest.class, TestSDD.class, TestASDD.class, ConversionTest.class, RddlsimTest.class})
public class AllTests {
}
