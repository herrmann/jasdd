package jsdd;

/**
 * Boolean logical sentence semantics.
 * 
 * @author Ricardo Herrmann
 */
public interface Sentence {

	static final String FALSE = "F";
	static final String TRUE = "T";

	boolean isTautology();
	boolean isUnsatisfiable();
	boolean isConsistent();

}
