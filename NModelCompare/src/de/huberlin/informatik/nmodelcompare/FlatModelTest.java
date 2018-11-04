package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class FlatModelTest
{
	private static final String SIMPLE = "testdata/special_cases_test.ecore";

	@Test
	void storesAllKnownNodes()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertEquals(7, nodes.size());
	}

	@Test
	void storesNodeNames()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertEquals("App", nodes.get(0).getName());
		assertEquals("renderTodoList", nodes.get(1).getName());
		assertEquals("sameAttrInTwoClasses", nodes.get(2).getName());
		assertEquals("TodoList", nodes.get(3).getName());
		assertEquals("sameAttrInTwoClasses", nodes.get(4).getName());
		assertEquals("titleProp", nodes.get(5).getName());
		assertEquals("itemsProp", nodes.get(6).getName());
	}

	@Test
	void storesNodeTypes()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertEquals("EClass", nodes.get(0).getType());
		assertEquals("EReference", nodes.get(1).getType());
		assertEquals("EAttribute", nodes.get(2).getType());
		assertEquals("EClass", nodes.get(3).getType());
		assertEquals("EAttribute", nodes.get(4).getType());
		assertEquals("EAttribute", nodes.get(5).getType());
		assertEquals("EAttribute", nodes.get(6).getType());
	}

	@Test
	void storesNodeParentNames()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		List<Node> nodes = flatModel.getNodes();
		assertNull(nodes.get(0).getParentName());
		assertEquals("App", nodes.get(1).getParentName());
		assertEquals("App", nodes.get(2).getParentName());
		assertNull(nodes.get(3).getParentName());
		assertEquals("TodoList", nodes.get(4).getParentName());
		assertEquals("TodoList", nodes.get(5).getParentName());
		assertEquals("TodoList", nodes.get(6).getParentName());
	}
}
