package de.huberlin.informatik.nmodelcompare;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.ENamedElementImpl;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class ModelLoaderTest
{
	private static final String SIMPLE = "testdata/special_cases_test.ecore";
	private static final String CSV = "testdata/hospitals.csv";

	@Test
	void canLoadAnEcoreModelFromFile()
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

	@Test
	void generatesMultipleEcoreModelsFromCsvFile() throws IOException
	{
		List<EPackage> models = ModelLoader.loadCsv(CSV);
		assertEquals(8, models.size());
		assertEquals("1,2,3,4,5,6,7,8", models.stream().map(EPackage::getName).collect(Collectors.joining(",")));
	}

	@Test
	void generatesMultipleEcoreModelsFromCsvFilex() throws IOException
	{
		EPackage model = ModelLoader.loadCsv(CSV).get(2);
		assertEquals(38, model.getEClassifiers().size());
		assertEquals("IntraWardStay", model.getEClassifiers().get(14).getName());
		assertEquals(7, model.getEClassifiers().get(14).eContents().size());
		assertEquals("roomOrUnitCheckInDate", ((EAttribute)model.getEClassifiers().get(14).eContents().get(1)).getName());
	}
}
