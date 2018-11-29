package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class NwmWeightTest
{
	@Test
	void singleElementTupleWeight() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");
		Node node = world.getNodes().get(0);
		HashSet<Node> tuple = new HashSet<Node>();
		tuple.add(node);
		Set<Set<Node>> set = new HashSet<Set<Node>>();
		set.add(tuple);

		NwmWeight matchesWeight = new NwmWeight(set, 4);
		assertEquals(0d, matchesWeight.sum());
	}

	@Test
	void disjointTuplesWeight() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");
		HashSet<Node> tuple = new HashSet<Node>();
		tuple.add(world.getNodes().get(3));
		tuple.add(world.getNodes().get(6));
		Set<Set<Node>> set = new HashSet<Set<Node>>();
		set.add(tuple);

		NwmWeight matchesWeight = new NwmWeight(set, 3);
		assertEquals(0d, matchesWeight.sum());
	}

	@Test
	void weightOfRubinTuple12() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");

		HashSet<Node> tuple = new HashSet<Node>();
		tuple.add(world.getNodes().get(0));
		tuple.add(world.getNodes().get(3));
		Set<Set<Node>> set = new HashSet<Set<Node>>();
		set.add(tuple);

		NwmWeight matchesWeight = new NwmWeight(set, 3);
		assertEquals(4d / 45, matchesWeight.sum());
	}

	@Test
	void weightOfRubinTuple34() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");

		HashSet<Node> tuple = new HashSet<Node>();
		tuple.add(world.getNodes().get(6));
		tuple.add(world.getNodes().get(9));
		Set<Set<Node>> set = new HashSet<Set<Node>>();
		set.add(tuple);

		NwmWeight matchesWeight = new NwmWeight(set, 3);
		assertEquals(2d / 9, matchesWeight.sum());
	}

	@Test
	void weightOfRubinTuple134() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");

		HashSet<Node> tuple = new HashSet<Node>();
		tuple.add(world.getNodes().get(0));
		tuple.add(world.getNodes().get(6));
		tuple.add(world.getNodes().get(9));
		Set<Set<Node>> set = new HashSet<Set<Node>>();
		set.add(tuple);

		NwmWeight matchesWeight = new NwmWeight(set, 3);
		assertEquals(2d / 9, matchesWeight.sum());
	}

	@Test
	void weightOfRubin1b() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");
		Similarities similarities = world.findSimilarities(2.5);
		Set<Set<Node>> matchesSet = new GreedyDistanceMatches(similarities).getMatchesSet();

		NwmWeight matchesWeight = new NwmWeight(matchesSet, 3);
		assertEquals(14d / 45, matchesWeight.sum());
	}

	@Test
	void weightOfRubin1d() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1c.csv");
		Similarities similarities = world.findSimilarities(2.5);
		Set<Set<Node>> matchesSet = new GreedyDistanceMatches(similarities).getMatchesSet();

		NwmWeight matchesWeight = new NwmWeight(matchesSet, 3);
		assertEquals(11d / 18, matchesWeight.sum());
	}

}
