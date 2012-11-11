package jsdd;

public interface Sentence {

	static final String FALSE = "F";
	static final String TRUE = "T";

	boolean isTautology();
	boolean isUnsatisfiable();

}
