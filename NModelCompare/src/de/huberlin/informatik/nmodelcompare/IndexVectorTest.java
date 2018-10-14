package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class IndexVectorTest
{
	private static final double E = 0.000000001;
	private static final String SIMPLE = "testdata/react_todo_app_2017021113_Akasky70_react_todo_app_step_4_bdeffc07.ecore";

	@Test
	void containsMetricalIndexes()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		IndexVectorFactory indexVectorFactory = new IndexVectorFactory();
		IndexVector indexVector = indexVectorFactory.vectorFor(flatModel.getNodes().get(2));
		assertEquals("TodoList".length(), indexVector.getCoord(VectorDimension.LON.ordinal()), E);
		assertEquals(2, indexVector.getCoord(VectorDimension.NOA.ordinal()), E);
	}
}
