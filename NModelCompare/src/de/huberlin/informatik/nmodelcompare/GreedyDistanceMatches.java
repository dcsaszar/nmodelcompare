package de.huberlin.informatik.nmodelcompare;

import java.util.Collection;
import java.util.stream.Collectors;

import org.javatuples.Pair;

public class GreedyDistanceMatches extends AbstractMatches
{
	public GreedyDistanceMatches(Similarities similarities)
	{
		super(similarities);
	}

	@Override
	Collection<Pair<Node, Node>> getPairsByPriority()
	{
		return getSimilarities().getAllIndexes().stream()
				.sorted((a, b) -> Double.compare(getSimilarities().getDistance(a), getSimilarities().getDistance(b))).collect(Collectors.toList());
	}

	@Override
	boolean isAcceptablePair(Pair<Node, Node> nodePair)
	{
		return true;
	}
}
