package jasdd.logic;

import java.util.Collections;
import java.util.List;

/**
 * Conjunction of many formulas.
 *
 * @author Ricardo Herrmann
 */
public class Conjunction implements Formula {

	private final List<Formula> elements;

	public Conjunction(final List<Formula> elements) {
		this.elements = Collections.unmodifiableList(elements);
	}

	public List<Formula> getElements() {
		return elements;
	}

}
