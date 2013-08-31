package jasdd.test;

import jasdd.JASDD;
import jasdd.algebraic.ASDD;
import jasdd.algebraic.DecompositionASDD;
import jasdd.bool.DecompositionSDD;
import jasdd.bool.SDD;
import jasdd.logic.Variable;
import jasdd.logic.VariableRegistry;
import jasdd.rddlsim.ASDDConverter;
import jasdd.viz.GraphvizDumper;
import jasdd.vtree.InternalAVTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.Tree;
import jasdd.vtree.VTree;
import jasdd.vtree.VTreeUtils;
import jasdd.vtree.ValueLeaf;
import jasdd.vtree.VariableLeaf;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

/**
 * CNF to SDD conversion tests
 *
 * @author Ricardo Herrmann
 */
public class ConversionTest {

	@Test
	public void fromCnf() throws FileNotFoundException {
		final VariableRegistry reg = new VariableRegistry();
		GraphvizDumper.setOutput("from_cnf.dot");
		// final Scanner scan = new Scanner(new File("testdata/cnf/c432.isc.cnf"));
		// final Scanner scan = new Scanner(new File("testdata/cnf/partial_c432.isc.cnf"));
		final Scanner scan = new Scanner(new File("testdata/cnf/simple.cnf"));
		InternalVTree vtree = null;
		SDD conjunction = JASDD.createTrue();
		int clauses = 0;
		while (scan.hasNext()) {
			if (scan.hasNextInt()) {
				int var;
				boolean eol = false;
				SDD disjunction = JASDD.createFalse();
				do {
					var = scan.nextInt();
					if (var != 0) {
						final DecompositionSDD sdd = DecompositionSDD.buildNormalized(vtree, var);
						disjunction = disjunction.or(sdd);
					} else {
						eol = true;
					}
				} while (!eol);
				if (disjunction.isConsistent()) {
					conjunction = conjunction.and(disjunction);
					GraphvizDumper.dump((DecompositionSDD) conjunction, reg, "conjunction.dot");
				}
				clauses++;
				// System.out.println(clauses);
			} else {
				final String cmd = scan.next();
				if ("p".equals(cmd) && scan.next("cnf") != null) {
					final int vars = scan.nextInt();
					final int totalClauses = scan.nextInt();
					// System.out.println(vars + " vars, " + totalClauses + " clauses");
					vtree = (InternalVTree) createVTree(vars);
				} else if ("c".equals(cmd) && scan.hasNextInt()) {
					final int index = scan.nextInt();
					final String name = scan.next();
					reg.register(index, name);
				}
			}
			if (scan.hasNextLine()) {
				scan.nextLine();
			}
		}
		// System.out.println("Size: " + conjunction.size());
	}

	private VTree createVTree(final int vars) {
		final Tree[] leaves = new Tree[vars];
		for (int i = 0; i < vars; i++) {
			leaves[i] = new VariableLeaf(i + 1);
		}
		return (VTree) VTreeUtils.skewedVTree(0, leaves, 0, leaves.length);
	}

}
