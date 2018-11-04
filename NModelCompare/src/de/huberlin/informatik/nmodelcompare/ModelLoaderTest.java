package de.huberlin.informatik.nmodelcompare;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENamedElementImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class ModelLoaderTest
{
	private static final String SIMPLE = "testdata/special_cases_test.ecore";

	@Test
	void canLoadAModelFromFile()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		assertEquals("special", model.getName());
		assertEquals(2, model.eContents().size());
		assertEquals("App", ((ENamedElementImpl)model.eContents().get(0)).getName());
		assertEquals("TodoList", ((ENamedElementImpl)model.eContents().get(1)).getName());
		List<EObject> allContents = new ArrayList<>();
		model.eAllContents().forEachRemaining(allContents::add);
		assertEquals(12, allContents.size());
		assertEquals("App", ((ENamedElementImpl)allContents.get(0)).getName());
		assertEquals("renderTodoList", ((ENamedElementImpl)allContents.get(1)).getName());
		assertEquals("TodoList", ((ENamedElementImpl)allContents.get(5)).getName());
		assertEquals("titleProp", ((ENamedElementImpl)allContents.get(8)).getName());
		assertEquals("itemsProp", ((ENamedElementImpl)allContents.get(10)).getName());
	}
}
