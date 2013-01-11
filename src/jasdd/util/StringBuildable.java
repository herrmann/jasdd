package jasdd.util;

/**
 * Type of classes that provide human-readable serialization of objects.
 * Implementations should also overwrite {@link Object#toString()}. 
 * 
 * @author Ricardo Herrmann
 */
public interface StringBuildable {

	StringBuilder toStringBuilder();

}
