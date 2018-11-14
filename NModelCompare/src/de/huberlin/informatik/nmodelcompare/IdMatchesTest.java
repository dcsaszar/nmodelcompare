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

		assertEquals(2, unsortedMatches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(9, unsortedMatches.stream().map(List::size).filter(s -> s == 1).count());
		assertEquals(13, unsortedMatches.stream().mapToInt(List::size).sum());

		List<List<Node>> matches = unsortedMatches.stream().sorted((o1, o2) -> o1.get(0).getFullName().compareTo(o2.get(0).getFullName()))
				.collect(Collectors.toList());
		List<List<Node>> multiMatches = matches.stream().filter(m -> m.size() > 1).collect(Collectors.toList());
		assertEquals("OneThreeTwo", multiMatches.get(0).get(0).getFullName());
		assertEquals("OneThreeTwo", multiMatches.get(0).get(1).getFullName());
		assertEquals("OneTwoThree", multiMatches.get(1).get(0).getFullName());
		assertEquals("OneTwoThree", multiMatches.get(1).get(1).getFullName());
	}

	@Test
	void remainingSimilaritiesOfTwoModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		Similarities remaining = new IdMatches(similarities).getRemaining();

		assertEquals(9, remaining.get2DWidth());
		assertEquals(9, remaining.getAllIndexes().size());
	}

	@Test
	void matchesOfThreeModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore", "testdata/partial_matches.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> unsortedMatches = new IdMatches(similarities).getMatches();

		assertEquals(4, unsortedMatches.stream().filter(m -> m.size() > 1).count());
		assertEquals(3, unsortedMatches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(1, unsortedMatches.stream().map(List::size).filter(s -> s == 3).count());

		List<List<Node>> matches = unsortedMatches.stream().sorted((o1, o2) -> o1.get(0).getFullName().compareTo(o2.get(0).getFullName()))
				.collect(Collectors.toList());
		assertEquals(Arrays.asList("EightTenEle", "FourFiveSix", "OneTwoThree"),
				matches.stream().filter(m -> m.size() == 2).map(l -> l.get(0).getFullName()).collect(Collectors.toList()));
		assertEquals(Arrays.asList("OneThreeTwo"),
				matches.stream().filter(m -> m.size() == 3).map(l -> l.get(0).getFullName()).collect(Collectors.toList()));
	}

	@Test
	void manyMatches()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/react_todo_app_2017021115_Akasky70_react_todo_app_step_10_c15f550b.ecore",
				"testdata/react_todo_app_2017021519_Akasky70_react_todo_app_step_15_4fe6b982.ecore",
				"testdata/react_todo_app_2017062714_master_c9ef612a.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		List<List<Node>> matches = new IdMatches(similarities).getMatches();

		assertEquals(54, matches.stream().filter(m -> m.size() > 1).count());
		assertEquals(44, matches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(10, matches.stream().map(List::size).filter(s -> s == 3).count());

		List<String> names = matches.stream().filter(m -> m.size() == 3).map(l -> l.get(0).getFullName()).sorted().collect(Collectors.toList());
		assertEquals(Arrays.asList(
				"App.renderTodoList", "Filter", "Filter.filterProp", "Footer.changeFilterProp", "Footer.countProp", "Footer.filterProp",
				"Footer.renderFilter", "TodoItem.dataProp", "TodoList.renderFooter", "TodoList.renderHeader"),
				names);
	}

	@Test
	void remainingSimilaritiesOfManyMatches()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/react_todo_app_2017021115_Akasky70_react_todo_app_step_10_c15f550b.ecore",
				"testdata/react_todo_app_2017021519_Akasky70_react_todo_app_step_15_4fe6b982.ecore",
				"testdata/react_todo_app_2017062714_master_c9ef612a.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		Similarities remaining = new IdMatches(similarities).getRemaining();

		assertEquals(66, remaining.get2DWidth());
		assertEquals(140, remaining.getAllIndexes().size());
	}
}
