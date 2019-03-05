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
		Map<Pair<Node, Node>, String> stringForPair = new HashMap<>();
		getSimilarities().getAllIndexes().forEach(nodePair -> {
			double weight = NwmWeight.nonNormalizedWeightForTuple(new HashSet<>(Arrays.asList(nodePair.getValue0(), nodePair.getValue1())));
			weightForPair.put(nodePair, weight);
			stringForPair.put(nodePair, nodePair.getValue0().getFullName() + nodePair.getValue1().getFullName());
		});
		return getSimilarities().getAllIndexes().stream()
				.sorted(Comparator.comparingDouble(pair -> -weightForPair.get(pair)).thenComparing(pair -> stringForPair.get(pair)))
				.collect(Collectors.toList());
	}

	@Override
	Pair<Node, Node> chooseNextPair(List<Pair<Node, Node>> pairs)
	{
		Pair<Node, Node> bestPair = pairs.get(0);
		return bestPair;
	}

	@Override
	boolean isAcceptablePair(Pair<Node, Node> nodePair)
	{
		return willImproveWeight(nodePair);
	}

	protected boolean willImproveWeight(Pair<Node, Node> nodePair)
	{
		double weightImprovement = computeWeightImprovementFor(nodePair);
		return weightImprovement > 0;
	}

	protected double computeWeightImprovementFor(Pair<Node, Node> nodePair)
	{
		Node nodeA = nodePair.getValue0();
		Node nodeB = nodePair.getValue1();

		Set<Node> nodeAGroup = getMatchesForNode(nodeA);
		Set<Node> nodeBGroup = getMatchesForNode(nodeB);
		Set<Node> mergedGroup = new HashSet<Node>(nodeAGroup);
		mergedGroup.addAll(nodeBGroup);

		double weightAGroup = NwmWeight.nonNormalizedWeightForTuple(nodeAGroup, true);
		double weightBGroup = NwmWeight.nonNormalizedWeightForTuple(nodeBGroup, true);
		double weightMergedGroup = NwmWeight.nonNormalizedWeightForTuple(mergedGroup, true);

		double weightImprovement = weightMergedGroup - (weightAGroup + weightBGroup);
		return weightImprovement;
	}
}
