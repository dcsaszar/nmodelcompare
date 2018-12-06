package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;

import org.javatuples.Pair;

public class WeightOptimizedMatches extends AbstractMatches
{
	public WeightOptimizedMatches(Similarities similarities)
	{
		super(similarities);
	}

	@Override
	List<Pair<Node, Node>> getPairsByPriority()
	{
		Map<Pair<Node, Node>, Double> weightForPair = new HashMap<>();
		getSimilarities().getAllIndexes().forEach(nodePair -> {
			double weight = NwmWeight.nonNormalizedWeightForTuple(new HashSet<>(Arrays.asList(nodePair.getValue0(), nodePair.getValue1())));
			weightForPair.put(nodePair, weight);
		});
		return getSimilarities().getAllIndexes().stream()
				.sorted((a, b) -> Double.compare(weightForPair.get(b), weightForPair.get(a))).collect(Collectors.toList());
	}

	@Override
	Pair<Node, Node> chooseNextPair(List<Pair<Node, Node>> pairs)
	{
		Pair<Node, Node> bestPair = pairs.get(0);
		if (!willImproveWeight(bestPair)) {
			return bestPair;
		}
		bestPair = pairs.stream().limit(4).max(Comparator.comparing(this::computeWeightImprovementFor)).get();
		return bestPair;
	}

	@Override
	boolean isAcceptablePair(Pair<Node, Node> nodePair)
	{
		return willImproveWeight(nodePair);
	}

	private boolean willImproveWeight(Pair<Node, Node> nodePair)
	{
		double weightImprovement = computeWeightImprovementFor(nodePair);
		return weightImprovement > 0;
	}

	private double computeWeightImprovementFor(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();

		Set<Node> nodeAGroup = getMatchesForNode(nodeA);
		Set<Node> nodeBGroup = getMatchesForNode(nodeB);
		Set<Node> mergedGroup = new HashSet<Node>(nodeAGroup);
		mergedGroup.addAll(nodeBGroup);

		double weightAGroup = NwmWeight.nonNormalizedWeightForTuple(nodeAGroup);
		double weightBGroup = NwmWeight.nonNormalizedWeightForTuple(nodeBGroup);
		double weightMergedGroup = NwmWeight.nonNormalizedWeightForTuple(mergedGroup);

		double weightImprovement = weightMergedGroup - (weightAGroup + weightBGroup);
		return weightImprovement;
	}
}
