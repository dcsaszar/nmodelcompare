package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.Test;

import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

class WeightOptimizedMatchesTest
{
	@Test
	void nwmWeightIsBetterThanWithGreedyMatches() throws IOException
	{
		NModelWorld world = NModelWorldLoader.loadChunks(Option.CLASSES_ONLY, "testdata/random.csv", 10).get(0);
		Similarities similarities = world.findSimilarities(2.5);
		Set<Set<Node>> greedyMatches = new GreedyDistanceMatches(similarities).getMatchesSet();
		Set<Set<Node>> weightOptimizedMatches = new WeightOptimizedMatches(similarities).getMatchesSet();
		NwmWeight greedyMatchesWeight = new NwmWeight(greedyMatches, 10, true);
		NwmWeight weightOptimizedMatchesWeight = new NwmWeight(weightOptimizedMatches, 10, true);

		assertEquals(0.2705d, greedyMatchesWeight.sum(), 0.0001d);
		assertTrue(weightOptimizedMatchesWeight.sum() > greedyMatchesWeight.sum());
		assertEquals(0.4841d, weightOptimizedMatchesWeight.sum(), 0.0001d);
	}

	@Test
	void nwmWeight() throws IOException
	{
		NModelWorld world = NModelWorldLoader.loadChunks(Option.CLASSES_ONLY, "testdata/random.csv", 10).get(0);

		Similarities similarities = world.findSimilarities(3.4);
		Set<Set<Node>> weightOptimizedMatches = new WeightOptimizedMatches(similarities).getMatchesSet();
		NwmWeight weightOptimizedMatchesWeight = new NwmWeight(weightOptimizedMatches, 10, true);

		assertEquals(1.0325d, weightOptimizedMatchesWeight.sum(), 0.0001d);
	}
}
