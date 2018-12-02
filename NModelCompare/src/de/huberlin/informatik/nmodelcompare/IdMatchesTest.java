package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
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
	void matchesOfOneHundredModels() throws IOException
	{
		NModelWorld world = NModelWorldLoader.load("testdata/random_subset.csv");
		Similarities similarities = world.findSimilarities(1.9);
		List<List<Node>> unsortedMatches = new IdMatches(similarities).getMatches();

		assertEquals(6, unsortedMatches.stream().filter(m -> m.size() > 1).count());
		assertEquals(6, unsortedMatches.stream().map(List::size).filter(s -> s == 2).count());

		List<List<Node>> matches = unsortedMatches.stream().sorted((o1, o2) -> o1.get(0).getFullName().compareTo(o2.get(0).getFullName()))
				.collect(Collectors.toList());
		assertEquals(Arrays.asList("[24,39]", "[24,39].24", "[24,39].39", "[4,39]", "[4,39].39", "[4,39].4"),
				matches.stream().filter(m -> m.size() == 2).map(l -> l.get(0).getFullName()).collect(Collectors.toList()));
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
		assertEquals(45, matches.stream().map(List::size).filter(s -> s == 2).count());
		assertEquals(9, matches.stream().map(List::size).filter(s -> s == 3).count());

		List<String> names = matches.stream().filter(m -> m.size() == 3).map(l -> l.get(0).getFullName()).sorted().collect(Collectors.toList());
		assertEquals(Arrays.asList(
				"App.renderTodoList", "Filter.filterProp", "Footer.changeFilterProp", "Footer.countProp", "Footer.filterProp",
				"Footer.renderFilter", "TodoItem.dataProp", "TodoList.renderFooter", "TodoList.renderHeader"),
				names);

		String allNames = matches.stream().filter(m -> m.size() == 2).map(l -> l.get(0).getFullName()).sorted().collect(Collectors.joining(","));
		assertEquals("App," + "App.renderKeyStrokeHandler," + "App.renderStateProvider," + "ButtonWrapper," + "ButtonWrapper.changeModeProp,"
				+ "ButtonWrapper.modeProp," + "CheckBox," + "CheckBox.handleChange," + "Filter," + "Filter.changeFilterProp," + "FilteredList,"
						+ "FilteredList.changeStatusProp," + "FilteredList.itemsProp," + "FilteredList.renderTodoItem," + "Footer,"
						+ "Footer.renderButtonWrapper," + "Header," + "Header.renderInputWrapper," + "Info," + "InputBox.handleChange,"
						+ "InputBox.handleKeyUp," + "InputWrapper," + "InputWrapper.addNewProp," + "InputWrapper.modeProp," + "InputWrapper.queryProp,"
						+ "InputWrapper.renderInputBox," + "InputWrapper.renderSearchBox," + "InputWrapper.setSearchQueryProp," + "KeyStrokeHandler,"
						+ "KeyStrokeHandler.handleKeyUp," + "SearchBox," + "SearchBox.queryProp," + "SearchBox.setSearchQueryProp," + "StateProvider,"
						+ "StateProvider.addNew," + "StateProvider.changeFilter," + "StateProvider.changeMode," + "StateProvider.changeStatus,"
						+ "StateProvider.setSearchQuery," + "TodoItem," + "TodoItem.changeStatusProp," + "TodoItem.renderCheckBox," + "TodoList,"
				+ "TodoList.renderFilteredList," + "TodoList.renderInfo", allNames);
	}

	@Test
	void remainingSimilaritiesOfManyMatches()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/react_todo_app_2017021115_Akasky70_react_todo_app_step_10_c15f550b.ecore",
				"testdata/react_todo_app_2017021519_Akasky70_react_todo_app_step_15_4fe6b982.ecore",
				"testdata/react_todo_app_2017062714_master_c9ef612a.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		Similarities remaining = new IdMatches(similarities).getRemaining();

		assertEquals(87, remaining.get2DWidth());
		assertEquals(305, remaining.getAllIndexes().size());
	}
}
