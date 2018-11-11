package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;

import org.javatuples.Pair;

public class IdMatches
{
	private List<List<Node>> _matches;
	private Similarities _similarities;
	private HashMap<Node, Set<Node>> _matchesByNode;

	public IdMatches(Similarities similarities)
	{
		_similarities = similarities;
		_matchesByNode = new HashMap<Node, Set<Node>>();
		similarities.getAllIndexes().forEach(nodePair -> {
			Node nodeA = nodePair.getValue0();
			Node nodeB = nodePair.getValue1();
			if (nodeA.isInSameModel(nodeB)) {
				return;
			}
			if (similarities.getDistance(nodePair) != 0) {
				return;
			}
			_matchesByNode.putIfAbsent(nodeA, new HashSet<Node>());
			_matchesByNode.putIfAbsent(nodeB, new HashSet<Node>());
			_matchesByNode.get(nodeA).add(nodeA);
			_matchesByNode.get(nodeA).add(nodeB);
			_matchesByNode.get(nodeA).addAll(_matchesByNode.get(nodeB));
			_matchesByNode.put(nodeB, _matchesByNode.get(nodeA));
		});
		Set<Set<Node>> matches = _matchesByNode.values().stream().collect(Collectors.toSet());
		_matches = matches.stream().map(indexSet -> indexSet.stream().collect(Collectors.toList()))
				.collect(Collectors.toList());
	}

	public List<List<Node>> getMatches()
	{
		return _matches;
	}
	
	public Similarities getRemaining() {
		Set<Pair<Node, Node>> keptPairs = _similarities.getAllIndexes().stream().filter(pair -> !this.isDismissableSimilarity(pair))
				.collect(Collectors.toSet());
		Set<Node> keptNodes = keptPairs.stream().flatMap(p -> Arrays.asList(new Node[] { p.getValue0(), p.getValue1() }).stream())
				.collect(Collectors.toSet());

		Similarities keptSimilarities = new Similarities(keptNodes.stream().collect(Collectors.toList()));
		keptPairs.stream().forEach(pair -> keptSimilarities.addDistance(pair, _similarities.getDistance(pair)));
		return keptSimilarities;
	}

	private boolean isDismissableSimilarity(Pair<Node, Node> pair)
	{
		Node nodeA = pair.getValue0();
		// dismiss, if nodeA is part of a match (i.e. it has already been matched)
		if (_matchesByNode.containsKey(nodeA)) {
			return true;
		}
		
		Node nodeB = pair.getValue1();
		// dismiss, if nodeB is part of a match, and nodeA belongs to any model of the
		// match node set (i.e. another candidate from nodeA's model has already been
		// chosen)
		if (_matchesByNode.containsKey(nodeB)) {
			return _matchesByNode.get(nodeB).stream().filter(node -> node.isInSameModel(nodeA)).findAny().isPresent();
		}

		return false;
	}
}
