package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;

import org.javatuples.Pair;

public abstract class AbstractMatches
{
	private MatchesList _matchesList;
	protected Similarities _similarities;
	private HashMap<Node, Set<Node>> _matchesByNode;
	private Similarities _remainingSimilarities;

	public AbstractMatches(Similarities similarities)
	{
		_similarities = similarities;
		_matchesByNode = new HashMap<>();
		getPairsByPriority().stream().forEach(nodePair -> {
			if (isAcceptableMatch(nodePair)) {
				addMatch(nodePair);
			}
		});

		Set<Pair<Node, Node>> keptPairs = getPairsByPriority().stream()
				.filter(pair -> isIdWithoutMatch(pair) || (isDisjoint(pair) && isFittingMatch(pair)))
				.collect(Collectors.toSet());
		Set<Node> keptNodes = keptPairs.stream().flatMap(p -> Arrays.asList(new Node[] { p.getValue0(), p.getValue1() }).stream())
				.collect(Collectors.toSet());

		Similarities keptSimilarities = new Similarities(keptNodes.stream().collect(Collectors.toList()));
		keptPairs.stream().forEach(pair -> keptSimilarities.addDistance(pair, _similarities.getDistance(pair)));
		_remainingSimilarities = keptSimilarities;

		Set<Set<Node>> uniqueMatches = _matchesByNode.values().stream().collect(Collectors.toSet());
		_matchesList = new MatchesList(uniqueMatches);
	}

	abstract Collection<Pair<Node, Node>> getPairsByPriority();

	abstract boolean isAcceptablePair(Pair<Node, Node> nodePair);

	public Similarities getSimilarities()
	{
		return _similarities;
	}

	private boolean isDisjoint(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();

		return !_matchesByNode.containsKey(nodeA) || (_matchesByNode.get(nodeA) != _matchesByNode.get(nodeB));
	}

	private boolean isIdWithoutMatch(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();
		return nodeA == nodeB && _matchesByNode.get(nodeA).size() == 1;
	}

	private boolean isFittingMatch(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();
		if (nodeA.isInSameModel(nodeB)) {
			return false;
		}
		Set<Node> group = new HashSet<Node>();
		group.addAll(_matchesByNode.getOrDefault(nodeA, new HashSet<Node>()));
		group.addAll(_matchesByNode.getOrDefault(nodeB, new HashSet<Node>()));
		group.remove(nodeA);
		group.remove(nodeB);
		return !group.stream().anyMatch(node -> node.isInSameModel(nodeA) || node.isInSameModel(nodeB));
	}

	private void addMatch(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();
		_matchesByNode.putIfAbsent(nodeA, new HashSet<Node>());
		_matchesByNode.putIfAbsent(nodeB, new HashSet<Node>());
		_matchesByNode.get(nodeA).add(nodeA);
		_matchesByNode.get(nodeA).add(nodeB);
		_matchesByNode.get(nodeA).addAll(_matchesByNode.get(nodeB));
		_matchesByNode.put(nodeB, _matchesByNode.get(nodeA));
	}

	private boolean isAcceptableMatch(Pair<Node, Node> nodePair)
	{
		if (!isAcceptablePair(nodePair)) {
			return false;
		}
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();
		if (nodeA == nodeB) {
			return true;
		}
		if (nodeA.isInSameModel(nodeB)) {
			return false;
		}
		return isFittingMatch(nodePair);
	}

	public List<List<Node>> getMatches()
	{
		return _matchesList.getAll();
	}
	
	public Similarities getRemaining() {
		return _remainingSimilarities;
	}

	public MatchesList getMatchesList()
	{
		return _matchesList;
	}
}
