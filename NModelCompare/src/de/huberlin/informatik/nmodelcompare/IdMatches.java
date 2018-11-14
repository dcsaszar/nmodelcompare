package de.huberlin.informatik.nmodelcompare;

import java.util.Collection;

import org.javatuples.Pair;

public class IdMatches extends AbstractMatches
{
	public IdMatches(Similarities similarities)
	{
		super(similarities);
	}

	@Override
	Collection<Pair<Node, Node>> getPairsByPriority()
	{
		return getSimilarities().getAllIndexes();
	}

	@Override
	boolean isAcceptablePair(Pair<Node, Node> nodePair)
	{
		return getSimilarities().getDistance(nodePair) == 0;
	}
}
