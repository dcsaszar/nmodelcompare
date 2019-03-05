package de.huberlin.informatik.nmodelcompare;

import java.util.Comparator;
import java.util.List;

import org.javatuples.Pair;

public class WeightOptimizedLookaheadMatches extends WeightOptimizedMatches
{
	private final int _lookahead;

	public WeightOptimizedLookaheadMatches(Similarities similarities, int lookahead)
	{
		super(similarities);
		this._lookahead = lookahead;
	}

	@Override
	Pair<Node, Node> chooseNextPair(List<Pair<Node, Node>> pairs)
	{
		Pair<Node, Node> bestPair = pairs.get(0);
		if (!willImproveWeight(bestPair)) {
			return bestPair;
		}
		return pairs.stream().limit(_lookahead).max(Comparator.comparingDouble(this::computeWeightImprovementFor)).get();
	}
}
