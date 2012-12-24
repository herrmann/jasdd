package jsdd.rddlsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jsdd.Variable;
import jsdd.algebraic.ASDD;
import jsdd.vtree.AVTree;
import dd.discrete.ADD;

public class ASDDConverter {

	private ADD context;

	public ASDDConverter() {
	}

	public ASDDConverter(final ADD context) {
		this.context = context;
	}

	public ASDD<Double> convert(final AVTree vtree) {
		return convert(vtree, new EnumerationPrinter());
	}

	public ASDD<Double> convert(final AVTree vtree, final Enumeration callback) {
		final List<Variable> vars = new ArrayList<Variable>(vtree.partitionVariables());
		enumerate(callback, new HashMap<Variable, Boolean>(), vars, 0);
		return null;
	}

	public interface Enumeration {
		void values(final Map<Variable, Boolean> values);
	}

	public class EnumerationPrinter implements Enumeration {
		@Override
		public void values(final Map<Variable, Boolean> values) {
			boolean first = true;
			for (final Entry<Variable, Boolean> entry : values.entrySet()) {
				if (!first) {
					System.out.print(",");
				}
				first = false;
				System.out.print(entry.getKey() + "=" + entry.getValue());
			}
			System.out.println();
		}
	}

	private void enumerate(final Enumeration callback, final Map<Variable, Boolean> values, final List<Variable> vars, final int index) {
		if (index < vars.size()) {
			final Variable var = vars.get(index);
			final HashMap<Variable, Boolean> copy = new HashMap<Variable, Boolean>(values);
			values.put(var, true);
			copy.put(var, false);
			enumerate(callback, values, vars, index + 1);
			enumerate(callback, copy, vars, index + 1);
		} else {
			callback.values(values);
		}
	}

}
