package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;

import org.javatuples.Pair;

public class GreedyDistanceMatches extends AbstractMatches
{
	public GreedyDistanceMatches(Similarities similarities)
	{
		super(similarities);
	}

	@Override
	List<Pair<Node, Node>> getPairsByPriority()
	{
		Map<Pair<Node, Node>, Double> distanceForPair = new HashMap<>();
		Map<Pair<Node, Node>, String> stringForPair = new HashMap<>();
		getSimilarities().getAllIndexes().forEach(nodePair -> {
			distanceForPair.put(nodePair, getSimilarities().getDistance(nodePair));
			stringForPair.put(nodePair, nodePair.getValue0().getFullName() + nodePair.getValue1().getFullName());
		});
		return getSimilarities().getAllIndexes().stream()
				.sorted(Comparator.comparingDouble(pair -> distanceForPair.get(pair)).thenComparing(pair -> stringForPair.get(pair)))
				.collect(Collectors.toList());
	}

	@Override
	boolean isAcceptablePair(Pair<Node, Node> nodePair)
	{
		return true;
	}
}
