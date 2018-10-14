package de.huberlin.informatik.nmodelcompare;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class NodeIndexTest
{
	private static final String SIMPLE = "testdata/react_todo_app_2017021113_Akasky70_react_todo_app_step_4_bdeffc07.ecore";

	@Test
	void indexesGivenNodes()
	{
		EPackageImpl model = ModelLoader.loadEcore(SIMPLE);
		FlatModel flatModel = new FlatModel(model);
		// NodeIndex nodeIndex = new NodeIndex();
		// nodeIndex.add(flatModel.getNodes());
	}
}
