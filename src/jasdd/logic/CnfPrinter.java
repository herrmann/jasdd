package jasdd.logic;

import java.util.Set;

/**
 * Printer of boolean formulas in CNF.
 * 
 * @author Ricardo Herrmann
 */
public class CnfPrinter {

	public static void print(final Set<Set<Literal>> cnf) {
		for (final Set<Literal> disj : cnf) {
			boolean first = true;
			for (final Literal lit : disj) {
				if (!first) {
					System.out.print(" ");
				}
				first = false;
				System.out.print(lit);
			}
			System.out.println();
		}
	}

}
