package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class FlatModelTest
{
	private static final String SIMPLE = "testdata/react_todo_app_2017021113_Akasky70_react_todo_app_step_4_bdeffc07.ecore";

	@Test
	void storesAllKnownNodes()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertEquals(5, nodes.size());
	}

	@Test
	void storesNodeNames()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertEquals("App", nodes.get(0).getName());
		assertEquals("renderTodoList", nodes.get(1).getName());
		assertEquals("TodoList", nodes.get(2).getName());
		assertEquals("titleProp", nodes.get(3).getName());
		assertEquals("itemsProp", nodes.get(4).getName());
	}

	@Test
	void storesNodeTypes()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertEquals("EClass", nodes.get(0).getType());
		assertEquals("EReference", nodes.get(1).getType());
		assertEquals("EClass", nodes.get(2).getType());
		assertEquals("EAttribute", nodes.get(3).getType());
		assertEquals("EAttribute", nodes.get(4).getType());
	}
}
