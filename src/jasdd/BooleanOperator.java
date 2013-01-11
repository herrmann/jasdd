package jasdd;

/**
 * Interface for any binary boolean operator over SDDs.
 * 
 * @author Ricardo Herrmann
 */
public interface BooleanOperator {

	boolean apply(boolean s1, boolean s2);

}
