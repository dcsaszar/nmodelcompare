package de.huberlin.informatik.nmodelcompare;

import java.util.List;

public class NModelWorld
{
	private NodeIndex _nodeIndex;
	private List<Node> _nodes;

	public NModelWorld(List<Node> nodes)
	{
		_nodes = nodes;
		_nodeIndex = new NodeIndex(nodes);
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
		int width = _nodes.size();
		Similarities similarities = new Similarities(width);
		for (int i = 0; i < width; i++) {
			Node node = getNodes().get(i);
			List<QueryResult> similar = _nodeIndex.findNearby(node, maxDistance);
			for (QueryResult result : similar) {
				double distance = result.getDistance();
				Node similarNode = result.getNode();
				if (!includeForSameModel && similarNode.isInSameModel(node)) {
					continue;
				}
				int j = getNodes().indexOf(similarNode);
				similarities.addDistance(i, j, distance);
			}
		}
		return similarities;
	}
}
