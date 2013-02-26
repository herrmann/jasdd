package jasdd.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Bidirectional map between variable names and indexes.
 *  
 * @author Ricardo Herrmann
 */
public class VariableRegistry {

	private Map<Integer, String> namesIndex = new HashMap<Integer, String>();
	private Map<String, Integer> index = new HashMap<String, Integer>();
	private int counter;

	public Variable register(final int id, final String name) {
		if (!namesIndex.containsKey(id) && !index.containsKey(name)) {
			index.put(name, id);
			namesIndex.put(id, name);
			return new Variable(id);
		} else {
			throw new AssertionError("Variable index and/or name already exist in registry");
		}
	}

	public Variable register(final String name) {
		if (index.containsKey(name)) {
			return new Variable(index.get(name));
		} else {
			int id = nextId();
			while (namesIndex.containsKey(id)) {
				id = nextId();
			}
			index.put(name, id);
			namesIndex.put(id, name);
			return new Variable(id);
		}
	}

	public boolean exists(final Variable var) {
		return exists(var.getIndex());
	}

	public boolean exists(final int id) {
		return namesIndex.containsKey(id);
	}

	public String name(final Variable var) {
		final int id = var.getIndex();
		return name(id);
	}

	public String name(final int id) {
		if (exists(id)) {
			return namesIndex.get(id);
		} else {
			return "#" + id;
		}
	}

	private synchronized int nextId() {
		return counter++;
	}

}
