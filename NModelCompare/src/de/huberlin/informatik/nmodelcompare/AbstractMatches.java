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
	private Set<Set<Node>> _matchesSet;

	public AbstractMatches(Similarities similarities)
	{
		_similarities = similarities;
		_matchesByNode = new HashMap<>();
		similarities.getNodes().forEach(node -> _matchesByNode.put(node, new HashSet<Node>(Arrays.asList(node))));
		List<Pair<Node, Node>> remainingPairs = getPairsByPriority();
		while (!remainingPairs.isEmpty()) {
			Pair<Node, Node> nodePair = chooseNextPair(remainingPairs);
			if (isAcceptableMatch(nodePair)) {
				addMatch(nodePair);
			}
			remainingPairs.remove(nodePair);
		}

		_remainingSimilarities = null;

		Set<Set<Node>> uniqueMatches = _matchesByNode.values().stream().collect(Collectors.toSet());
		_matchesSet = uniqueMatches;
		_matchesList = new MatchesList(uniqueMatches);
	}

	Pair<Node, Node> chooseNextPair(List<Pair<Node, Node>> pairs)
	{
		return pairs.get(0);
	}

	abstract List<Pair<Node, Node>> getPairsByPriority();

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
		group.addAll(_matchesByNode.get(nodeA));
		group.addAll(_matchesByNode.get(nodeB));
		long modelCount = group.stream().map(Node::getModelId).distinct().count();
		return group.size() == modelCount;
	}

	private void addMatch(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();
		Set<Node> nodeAGroup = _matchesByNode.get(nodeA);
		Set<Node> nodeBGroup = _matchesByNode.get(nodeB);
		nodeAGroup.add(nodeB);
		nodeAGroup.addAll(nodeBGroup);
		nodeAGroup.stream().forEach(node -> _matchesByNode.put(node, nodeAGroup));
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

	Set<Node> getMatchesForNode(Node node)
	{
		return Collections.unmodifiableSet(_matchesByNode.get(node));
	}

	public List<List<Node>> getMatches()
	{
		return _matchesList.getAll();
	}

	public Similarities getRemaining()
	{
		if (_remainingSimilarities == null) {
			Set<Pair<Node, Node>> keptPairs = getPairsByPriority().stream()
					.filter(pair -> isIdWithoutMatch(pair) || (isDisjoint(pair) && isFittingMatch(pair))).collect(Collectors.toSet());
			Set<Node> keptNodes = keptPairs.stream().flatMap(p -> Arrays.asList(new Node[] { p.getValue0(), p.getValue1() }).stream())
					.collect(Collectors.toSet());

			Similarities keptSimilarities = new Similarities(keptNodes.stream().collect(Collectors.toList()));
			keptPairs.stream().forEach(pair -> keptSimilarities.addDistance(pair, _similarities.getDistance(pair)));
			_remainingSimilarities = keptSimilarities;
		}
		return _remainingSimilarities;
	}

	public MatchesList getMatchesList()
	{
		return _matchesList;
	}

	public Set<Set<Node>> getMatchesSet()
	{
		return _matchesSet;
	}
}
