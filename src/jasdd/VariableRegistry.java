package jasdd;

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

	public Variable register(final String name) {
		if (index.containsKey(name)) {
			return new Variable(index.get(name));
		} else {
			final int id = nextId();
			index.put(name, id);
			namesIndex.put(id, name);
			return new Variable(id);
		}
	}

	public boolean exists(final Variable var) {
		return namesIndex.containsKey(var.getIndex());
	}

	public String name(final Variable var) {
		final int id = var.getIndex();
		if (exists(var)) {
			return namesIndex.get(id);
		} else {
			return "#" + id;
		}
	}

	private synchronized int nextId() {
		return counter++;
	}

}
