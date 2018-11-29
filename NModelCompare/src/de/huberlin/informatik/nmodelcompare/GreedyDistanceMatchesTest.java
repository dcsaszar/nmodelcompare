package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class GreedyDistanceMatchesTest
{
	@Test
	void matchesOfTwoModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> unsortedMatches = new GreedyDistanceMatches(similarities).getMatches();

		assertEquals(0, unsortedMatches.stream().filter(m -> m.size() > 2).count());
		assertEquals(2, unsortedMatches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(9, unsortedMatches.stream().map(List::size).filter(s -> s == 1).count());
		assertEquals(13, unsortedMatches.stream().mapToInt(List::size).sum());
	}

	@Test
	void matchesOfThreeModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore", "testdata/partial_matches.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> unsortedMatches = new GreedyDistanceMatches(similarities).getMatches();

		assertEquals(0, unsortedMatches.stream().filter(m -> m.size() > 3).count());
		assertEquals(4, unsortedMatches.stream().filter(m -> m.size() > 1).count());
		assertEquals(3, unsortedMatches.stream().filter(m -> m.size() == 2).count());
		assertEquals(1, unsortedMatches.stream().filter(m -> m.size() == 3).count());
	}

	@Test
	void manyMatches()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/react_todo_app_2017021115_Akasky70_react_todo_app_step_10_c15f550b.ecore",
				"testdata/react_todo_app_2017021519_Akasky70_react_todo_app_step_15_4fe6b982.ecore",
				"testdata/react_todo_app_2017062714_master_c9ef612a.ecore");
		Similarities similarities = world.findSimilarities(2.4);
		List<List<Node>> matches = new GreedyDistanceMatches(similarities).getMatches();

		assertEquals(0, matches.stream().filter(m -> m.size() > 3).count());
		assertEquals(24, matches.stream().map(List::size).filter(s -> s == 3).count());
		assertEquals(31, matches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(55, matches.stream().filter(m -> m.size() > 1).count());
	}

	@Test
	void fewMatchesContainNoDuplicates() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/small.csv");
		Similarities similarities = world.findSimilarities(2);
		List<List<Node>> matches = new GreedyDistanceMatches(similarities).getMatches();
		List<Node> listOfNodes = matches.stream().flatMap(Collection::stream).collect(Collectors.toList());
		Set<Node> setOfNodes = matches.stream().flatMap(Collection::stream).collect(Collectors.toSet());

		assertEquals(setOfNodes.size(), listOfNodes.size());
	}

	@Test
	void matchesContainNoDuplicates() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/hospitals_subset.csv");
		Similarities similarities = world.findSimilarities(2.4);
		List<List<Node>> matches = new GreedyDistanceMatches(similarities).getMatches();
		List<Node> listOfNodes = matches.stream().flatMap(Collection::stream).collect(Collectors.toList());
		Set<Node> setOfNodes = matches.stream().flatMap(Collection::stream).collect(Collectors.toSet());

		assertEquals(setOfNodes.size(), listOfNodes.size());
	}

	@Test
	void mergesAccordingToRubin1b() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1a.csv");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> matches = new GreedyDistanceMatches(similarities).getMatches();
		List<List<Node>> classMatches = matches.stream().filter(match -> match.stream().anyMatch(node -> node.getType() == "EClass"))
				.collect(Collectors.toList());

		assertEquals(2, classMatches.size());
		assertEquals(2, classMatches.get(0).size());
		assertEquals("CareTaker-M1", classMatches.get(0).get(0).getDescription());
		assertEquals("Physician-M2", classMatches.get(0).get(1).getDescription());
		assertEquals(2, classMatches.get(1).size());
		assertEquals("Nurse-M2", classMatches.get(1).get(0).getDescription());
		assertEquals("Nurse-M3", classMatches.get(1).get(1).getDescription());
	}

	@Test
	void mergesAccordingToRubin1d() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/rubin1c.csv");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> matches = new GreedyDistanceMatches(similarities).getMatches();
		List<List<Node>> classMatches = matches.stream().filter(match -> match.stream().anyMatch(node -> node.getType() == "EClass"))
				.collect(Collectors.toList());

		assertEquals(2, classMatches.size());
		assertEquals(3, classMatches.get(0).size());
		assertEquals(1, classMatches.get(1).size());
	}
}
