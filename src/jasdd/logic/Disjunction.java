package jasdd.logic;

import java.util.Collections;
import java.util.List;

/**
 * Disjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Disjunction implements Formula {

	private final List<Formula> elements;

	public Disjunction(final List<Formula> elements) {
		this.elements = Collections.unmodifiableList(elements);
	}

	public List<Formula> getElements() {
		return elements;
	}

}
