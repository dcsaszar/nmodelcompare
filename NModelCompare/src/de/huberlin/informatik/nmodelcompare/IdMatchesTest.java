package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class IdMatchesTest
{
	@Test
	void matchesOfTwoModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> unsortedMatches = new IdMatches(similarities).getMatches();
		List<List<Node>> matches = unsortedMatches.stream().sorted((o1, o2) -> o1.get(0).getFullName().compareTo(o2.get(0).getFullName()))
				.collect(Collectors.toList());

		assertEquals(2, matches.size());
		assertEquals(2, matches.get(0).size());
		assertEquals(2, matches.get(1).size());
		assertEquals("OneThreeTwo", matches.get(0).get(0).getFullName());
		assertEquals("OneThreeTwo", matches.get(0).get(1).getFullName());
		assertEquals("OneTwoThree", matches.get(1).get(0).getFullName());
		assertEquals("OneTwoThree", matches.get(1).get(1).getFullName());
	}

	@Test
	void remainingSimilaritiesOfTwoModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		Similarities remaining = new IdMatches(similarities).getRemaining();

		assertEquals(9, remaining.get2DWidth());
	}

	@Test
	void matchesOfThreeModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore", "testdata/partial_matches.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> unsortedMatches = new IdMatches(similarities).getMatches();
		List<List<Node>> matches = unsortedMatches.stream().sorted((o1, o2) -> o1.get(0).getFullName().compareTo(o2.get(0).getFullName()))
				.collect(Collectors.toList());

		assertEquals(4, matches.size());
		assertEquals(Arrays.asList(2, 2, 3, 2), matches.stream().map(List::size).collect(Collectors.toList()));
		assertEquals(Arrays.asList("EightTenEle", "FourFiveSix", "OneThreeTwo", "OneTwoThree"),
				matches.stream().map(l -> l.get(0).getFullName()).collect(Collectors.toList()));
	}

	@Test
	void manyMatches()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/react_todo_app_2017021115_Akasky70_react_todo_app_step_10_c15f550b.ecore",
				"testdata/react_todo_app_2017021519_Akasky70_react_todo_app_step_15_4fe6b982.ecore",
				"testdata/react_todo_app_2017062714_master_c9ef612a.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> matches = new IdMatches(similarities).getMatches();
		List<String> names = matches.stream().map(l -> l.get(0).getFullName()).sorted().collect(Collectors.toList());

		assertEquals(54, matches.size());
		assertEquals(44, matches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(10, matches.stream().map(List::size).filter(s -> s == 3).count());
		assertEquals("InputWrapper.queryProp", names.get(30));
	}
}
