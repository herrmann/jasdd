package jasdd.tools;

import jasdd.util.Pair;
import jasdd.vtree.InternalTree;
import jasdd.vtree.InternalVTree;
import jasdd.vtree.VTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph of operations over vtrees.
 *
 * @author Ricardo Herrmann
 */
public class OperationsGraph {

	// TODO: create a proper generic Graph class
	private static final Map<VTree, Integer> sizes = new HashMap<VTree, Integer>();
	private static final Map<VTree, List<Pair<InternalVTree, EdgeInfo>>> graph = new HashMap<VTree, List<Pair<InternalVTree, EdgeInfo>>>();

	public void addNode(final InternalVTree vtree, final int size) {
		sizes.put(vtree, size);
	}

	public boolean contains(final InternalTree<VTree> vtree) {
		return sizes.containsKey(vtree);
	}

	public void addEdge(final InternalVTree parent, final InternalVTree child, final EdgeInfo edgeInfo) {
		final Pair<InternalVTree, EdgeInfo> pair = Pair.create(child, edgeInfo);
		// TODO: use a MultiMap implementation
		if (graph.containsKey(parent)) {
			final List<Pair<InternalVTree, EdgeInfo>> list = graph.get(parent);
			list.add(pair);
			graph.put(parent, list);
		} else {
			final List<Pair<InternalVTree, EdgeInfo>> list = new ArrayList<Pair<InternalVTree, EdgeInfo>>();
			list.add(pair);
			graph.put(parent, list);
		}
	}

}
