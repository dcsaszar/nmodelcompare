package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.savarese.spatial.KDTree;
import com.savarese.spatial.NearestNeighbors;
import com.savarese.spatial.NearestNeighbors.Entry;

public class NodeIndex
{
	private final KDTree<Double, IndexVector, ArrayList<Node>> _tree;
	private IndexVectorFactory _indexVectorFactory;
	private NearestNeighbors<Double, IndexVector, ArrayList<Node>> _nearestNeighbors;

	public NodeIndex(List<Node> nodes)
	{
		_tree = new KDTree<>();
		_nearestNeighbors = new NearestNeighbors<>();

		_indexVectorFactory = new IndexVectorFactory(nodes);
		nodes.stream().forEach(node -> add(node));
		_tree.optimize();
	}

	private void add(Node node)
	{
		IndexVector indexVector = _indexVectorFactory.vectorFor(node);
		if (_tree.containsKey(indexVector))
		{
			ArrayList<Node> nodes = _tree.get(indexVector);
			nodes.add(node);
			_tree.put(indexVector, nodes);
		}
		else
		{
			_tree.put(indexVector, new ArrayList<Node>(Arrays.asList(node)));
		}
	}

	public List<QueryResult> findNearby(Node node, double distance)
	{
		// TODO possible optimizations:
		// * query with bounding box around queryPoint
		// * iterate with increasing numNeighbors
		IndexVector queryPoint = _indexVectorFactory.vectorFor(node);
		Stream<Entry<Double, IndexVector, ArrayList<Node>>> entries = Arrays.stream(_nearestNeighbors.get(_tree, queryPoint, _tree.size(), false));

		Stream<List<QueryResult>> results = entries.filter(entry -> entry.getDistance() <= distance)
				.map(entry -> entry.getNeighbor().getValue().stream().map(n -> new QueryResult(n, entry.getDistance())).collect(Collectors.toList()));

		return results.flatMap(List::stream).collect(Collectors.toList());
	}

	public List<QueryResult> findAllByDistance(Node node)
	{
		IndexVector queryPoint = _indexVectorFactory.vectorFor(node);
		Stream<Entry<Double, IndexVector, ArrayList<Node>>> entries = Arrays.stream(_nearestNeighbors.get(_tree, queryPoint, _tree.size(), false));

		Stream<List<QueryResult>> results = entries
				.map(entry -> entry.getNeighbor().getValue().stream().map(n -> new QueryResult(n, entry.getDistance())).collect(Collectors.toList()));
		
		return results.flatMap(List::stream).collect(Collectors.toList());
	}

	public int getSize()
	{
		return _tree.values().stream().mapToInt(v -> v.size()).sum();
	}
}
