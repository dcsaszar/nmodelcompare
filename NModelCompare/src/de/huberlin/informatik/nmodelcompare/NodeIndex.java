package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.savarese.spatial.KDTree;
import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;

public class NodeIndex
{
	private final KDTree<Double, IndexVector, List<Node>> _tree = new KDTree<>();
	private IndexVectorFactory _indexVectorFactory;
	private NearestNeighbors<Double, IndexVector, List<Node>> _nearestNeighbors = new NearestNeighbors<>();
	private HashMap<Node, IndexVector> _indexVectorForNode;

	public NodeIndex(List<Node> nodes)
	{
		_indexVectorForNode = new HashMap<Node, IndexVector>(nodes.size());

		_indexVectorFactory = new IndexVectorFactory(nodes);
		nodes.stream().forEach(node -> add(node));
	}

	private void add(Node node)
	{
		IndexVector indexVector = _indexVectorFactory.vectorFor(node);
		_indexVectorForNode.put(node, indexVector);
		if (_tree.containsKey(indexVector))
		{
			_tree.get(indexVector).add(node);
		}
		else
		{
			_tree.put(indexVector, new ArrayList<Node>(Arrays.asList(node)));
		}
	}

	public List<QueryResult> findNearby(Node node, double distance)
	{
		IndexVector queryPoint = _indexVectorForNode.get(node);

		Entry<Double, IndexVector, List<Node>>[] nearestNeighbors = _nearestNeighbors.get(_tree, queryPoint, _tree.size(), false);
		Stream<Entry<Double, IndexVector, List<Node>>> entries = Arrays.stream(nearestNeighbors);

		Stream<List<QueryResult>> results = entries.filter(entry -> entry.getDistance() <= distance)
				.map(entry -> entry.getNeighbor().getValue().stream().map(n -> new QueryResult(n, entry.getDistance())).collect(Collectors.toList()));

		return results.flatMap(List::stream).collect(Collectors.toList());
	}

	public List<QueryResult> findAllByDistance(Node node)
	{
		IndexVector queryPoint = _indexVectorForNode.get(node);
		Stream<Entry<Double, IndexVector, List<Node>>> entries = Arrays.stream(_nearestNeighbors.get(_tree, queryPoint, _tree.size(), false));

		Stream<List<QueryResult>> results = entries
				.map(entry -> entry.getNeighbor().getValue().stream().map(n -> new QueryResult(n, entry.getDistance())).collect(Collectors.toList()));
		
		return results.flatMap(List::stream).collect(Collectors.toList());
	}

	public int getSize()
	{
		return _tree.values().stream().mapToInt(v -> v.size()).sum();
	}
}
