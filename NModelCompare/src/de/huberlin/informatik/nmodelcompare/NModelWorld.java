package de.huberlin.informatik.nmodelcompare;

import java.util.List;

public class NModelWorld
{
	private final NodeIndex _nodeIndex;
	private final List<Node> _nodes;
	private final int _numberOfInputModels;

	public NModelWorld(List<Node> nodes, int numberOfInputModels)
	{
		_nodes = nodes;
		_nodeIndex = new NodeIndex(nodes);
		_numberOfInputModels = numberOfInputModels;
	}

	public int getNumberOfInputModels()
	{
		return _numberOfInputModels;
	}

	public List<Node> getNodes()
	{
		return _nodes;
	}

	public NodeIndex getNodeIndex()
	{
		return _nodeIndex;
	}

	public Similarities findInterModelSimilarities(double maxDistance) {
		return findSimilarities(maxDistance, false);
	}

	public Similarities findSimilarities(double maxDistance) {
		return findSimilarities(maxDistance, true);
	}

	private Similarities findSimilarities(double maxDistance, boolean includeForSameModel)
	{
		Similarities similarities = new Similarities(getNodes());
		for (Node node : getNodes()) {
			List<QueryResult> similar = _nodeIndex.findNearby(node, maxDistance);
			for (QueryResult result : similar) {
				double distance = result.getDistance();
				Node similarNode = result.getNode();
				if (!includeForSameModel && similarNode.isInSameModel(node)) {
					continue;
				}
				similarities.addDistance(node, similarNode, distance);
				similarities.addDistance(similarNode, node, distance);
			}
		}
		return similarities;
	}
}
