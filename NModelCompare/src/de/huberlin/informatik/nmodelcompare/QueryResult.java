package de.huberlin.informatik.nmodelcompare;

public class QueryResult
{
	private final Node _node;
	private final double _distance;

	public QueryResult(Node node, double distance)
	{
		_node = node;
		_distance = distance;
	}

	public double getDistance()
	{
		return _distance;
	}

	public Node getNode()
	{
		return _node;
	}
}
