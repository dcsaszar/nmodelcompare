package de.huberlin.informatik.nmodelcompare;

import java.util.*;

import org.javatuples.Pair;

public class Similarities
{
	private HashMap<Pair<Node, Node>, Double> _matrix;
	private int _width;
	private Double _maxDistance;
	private List<Node> _nodes;

	public Similarities(List<Node> nodes)
	{
		_maxDistance = 0d;
		_matrix = new HashMap<Pair<Node, Node>, Double>();
		_width = nodes.size();
		_nodes = nodes;
	}

	public void addDistance(Node nodeA, Node nodeB, double distance)
	{
		addDistance(new Pair<>(nodeA, nodeB), distance);
	}

	public void addDistance(Pair<Node, Node> index, double distance)
	{
		_maxDistance = Math.max(distance, _maxDistance);
		_matrix.put(index, distance);
	}

	public int get2DWidth()
	{
		return _width;
	}

	public Double getDistance(Pair<Node, Node> index)
	{
		return _matrix.get(index);
	}

	public Double getDistance(int nodeIndexX, int nodeIndexY)
	{
		return getDistance(new Pair<>(_nodes.get(nodeIndexX), _nodes.get(nodeIndexY)));
	}

	public Double getNormalizedDistance(Pair<Node, Node> index)
	{
		Double d = getDistance(index);
		if (d == null) {
			return null;
		}
		if (_maxDistance == 0) {
			return 1d;
		}
		return d / _maxDistance;
	}

	public Double getNormalizedDistance(int nodeIndexX, int nodeIndexY)
	{
		return getNormalizedDistance(new Pair<>(_nodes.get(nodeIndexX), _nodes.get(nodeIndexY)));
	}

	public Set<Pair<Node, Node>> getAllIndexes()
	{
		return _matrix.keySet();
	}

	public Pair<Node, Node> getNodePair(Pair<Integer, Integer> index)
	{
		return new Pair<>(_nodes.get(index.getValue0()), _nodes.get(index.getValue1()));
	}

	public List<Node> getNodes()
	{
		return _nodes;
	}
}
