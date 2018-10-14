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
	private static final String SIMPLE = "testdata/react_todo_app_2017021113_Akasky70_react_todo_app_step_4_bdeffc07.ecore";

	@Test
	void canLoadAModelFromFile()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		assertEquals("react_todo_app_2017021113_Akasky70_react_todo_app_step_4_bdeffc07", model.getName());
		assertEquals(2, model.eContents().size());
		assertEquals("App", ((ENamedElementImpl)model.eContents().get(0)).getName());
		assertEquals("TodoList", ((ENamedElementImpl)model.eContents().get(1)).getName());
		List<EObject> allContents = new ArrayList<>();
		model.eAllContents().forEachRemaining(allContents::add);
		assertEquals(8, allContents.size());
		assertEquals("App", ((ENamedElementImpl)allContents.get(0)).getName());
		assertEquals("renderTodoList", ((ENamedElementImpl)allContents.get(1)).getName());
		assertEquals("TodoList", ((ENamedElementImpl)allContents.get(3)).getName());
		assertEquals("titleProp", ((ENamedElementImpl)allContents.get(4)).getName());
		assertEquals("itemsProp", ((ENamedElementImpl)allContents.get(6)).getName());
	}
}
