package jasdd.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Run all the tests.
 * 
 * @author Ricardo Herrmann
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({UtilsTest.class, VTreeTest.class, TestSDD.class, TestASDD.class})
public class AllTests {
}
