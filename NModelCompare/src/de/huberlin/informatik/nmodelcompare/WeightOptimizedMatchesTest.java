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
		NModelWorld world = NModelWorldLoader.load(Option.CLASSES_ONLY, "testdata/random_subset.csv");
		Similarities similarities = world.findSimilarities(2.5);
		Set<Set<Node>> greedyMatches = new GreedyDistanceMatches(similarities).getMatchesSet();
		Set<Set<Node>> weightOptimizedMatches = new WeightOptimizedMatches(similarities).getMatchesSet();
		NwmWeight greedyMatchesWeight = new NwmWeight(greedyMatches, 100);
		NwmWeight weightOptimizedMatchesWeight = new NwmWeight(weightOptimizedMatches, 100);

		assertEquals(0.0008d, greedyMatchesWeight.sum(), 0.0001d);
		assertTrue(weightOptimizedMatchesWeight.sum() > greedyMatchesWeight.sum());
		assertEquals(0.0042d, weightOptimizedMatchesWeight.sum(), 0.0001d);
	}

	@Test
	void nwmWeight() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load(Option.CLASSES_ONLY, "testdata/random_sample270.csv");

		Similarities similarities = world.findSimilarities(3.7);
		Set<Set<Node>> weightOptimizedMatches = new WeightOptimizedMatches(similarities).getMatchesSet();
		NwmWeight weightOptimizedMatchesWeight = new NwmWeight(weightOptimizedMatches, 100);

		assertEquals(0.0092d, weightOptimizedMatchesWeight.sum(), 0.0003d); // TODO: unstable
	}
}
