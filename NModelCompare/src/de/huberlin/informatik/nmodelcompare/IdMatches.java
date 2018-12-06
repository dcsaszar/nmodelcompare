package de.huberlin.informatik.nmodelcompare;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class IdMatches extends AbstractMatches
{
	public IdMatches(Similarities similarities)
	{
		super(similarities);
	}

	@Override
	List<Pair<Node, Node>> getPairsByPriority()
	{
		return new ArrayList<>(getSimilarities().getAllIndexes());
	}

	@Override
	boolean isAcceptablePair(Pair<Node, Node> nodePair)
	{
		return getSimilarities().getDistance(nodePair) == 0;
	}
}
