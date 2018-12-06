package de.huberlin.informatik.nmodelcompare.measurement;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

public class ReproduceRubinPw
{
	public static void main(String... args) throws IOException
	{
		NModelWorld world = NModelWorldLoader.load(Option.CLASSES_ONLY, "testdata/random.csv");
		List<Node> nodes = world.getNodes();
		System.out.println("Input (" + world.getNumberOfInputModels() + " models, " + nodes.size() + " classes): " + nodes);
		Set<List<Node>> matches = new HashSet<>();
		while (nodes.size() >= 2) {
			Node nodeA = nodes.get(0);
			nodes.remove(nodeA);
			Integer modelId = nodeA.getModelId();
			Stream<Node> validNodes = nodes.parallelStream().filter(nodeB -> modelId != nodeB.getModelId());
			Optional<Node> bestNode = validNodes
					.max(Comparator.comparingDouble(nodeB -> NwmWeight.nonNormalizedWeightForTuple(Arrays.asList(nodeA, nodeB))));
			if (bestNode.isPresent()) {
				List<Node> bestPair = Arrays.asList(nodeA, bestNode.get());
				double weight = NwmWeight.nonNormalizedWeightForTuple(bestPair);
				if (weight > 0) {
					nodes.remove(bestNode.get());
					matches.add(bestPair);
				}
			}
			double weightSum = new NwmWeight(matches, world.getNumberOfInputModels()).sum();
			System.out.println("Left: " + nodes.size() + " Weight: " + weightSum);
		}
		System.out.println("Matches (" + matches.size() + "): " + matches);
	}
}
