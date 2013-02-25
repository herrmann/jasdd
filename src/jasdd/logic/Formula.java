package jasdd.logic;

import java.util.Set;

/**
 * Type of Boolean formulas.
 *
 * @author Ricardo Herrmann
 */
public interface Formula {

	Formula trim();

	Set<Set<Literal>> toCnf();

}
