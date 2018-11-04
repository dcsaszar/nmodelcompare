package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class NodeIndexTest
{
	@Test
	void indexesAllNodes()
	{
		EPackageImpl model = ModelLoader.loadEcore("testdata/special_cases_test.ecore");
		FlatModel flatModel = new FlatModel(model);
		assertEquals(7, flatModel.getNodes().size());
		NodeIndex nodeIndex = new NodeIndex(flatModel.getNodes());
		assertEquals(7, nodeIndex.getSize());
	}

	@Test
	void indexesAllNodesOfMultipleModels()
	{
		FlatModel flatModelA = new FlatModel(ModelLoader.loadEcore("testdata/special_cases_test.ecore"));
		FlatModel flatModelB = new FlatModel(ModelLoader.loadEcore("testdata/few_words.ecore"));
		FlatModel flatModelC = new FlatModel(ModelLoader.loadEcore("testdata/many_words.ecore"));
		List<Node> nodes = flatModelA.getNodes();
		nodes.addAll(flatModelB.getNodes());
		nodes.addAll(flatModelC.getNodes());
		assertEquals(20, nodes.size());
		NodeIndex nodeIndex = new NodeIndex(nodes);
		assertEquals(20, nodeIndex.getSize());
	}

	@Test
	void lexicographicalDistanceWithSmallVectorDoesntDependOnWordCount()
	{
		EPackageImpl model = ModelLoader.loadEcore("testdata/few_words.ecore");
		FlatModel flatModel = new FlatModel(model);
		NodeIndex nodeIndex = new NodeIndex(flatModel.getNodes());
		List<QueryResult> nearestNodes = nodeIndex.findAllByDistance(flatModel.getNodes().get(0));
		assertEquals(0, nearestNodes.get(0).getDistance(), 0.0001);
		assertEquals(1.4142, nearestNodes.get(1).getDistance(), 0.0001);
	}

	@Test
	void lexicographicalDistanceWithLargeVectorDoesntDependOnWordCount()
	{
		EPackageImpl model = ModelLoader.loadEcore("testdata/many_words.ecore");
		FlatModel flatModel = new FlatModel(model);
		NodeIndex nodeIndex = new NodeIndex(flatModel.getNodes());
		List<QueryResult> nearestNodes = nodeIndex.findAllByDistance(flatModel.getNodes().get(0));
		assertEquals(0, nearestNodes.get(0).getDistance(), 0.0001);
		assertEquals(1.4142, nearestNodes.get(1).getDistance(), 0.0001);
	}

	@Test
	void canFindNodesWithinDistance()
	{
		EPackageImpl model = ModelLoader.loadEcore("testdata/many_words.ecore");
		FlatModel flatModel = new FlatModel(model);
		NodeIndex nodeIndex = new NodeIndex(flatModel.getNodes());
		assertEquals(1, nodeIndex.findNearby(flatModel.getNodes().get(0), 0).size());
		assertEquals(1, nodeIndex.findNearby(flatModel.getNodes().get(0), 1.41).size());
		assertEquals(2, nodeIndex.findNearby(flatModel.getNodes().get(0), 1.42).size());
		assertEquals(2, nodeIndex.findNearby(flatModel.getNodes().get(0), 2.64).size());
		assertEquals(3, nodeIndex.findNearby(flatModel.getNodes().get(0), 2.65).size());
		assertEquals(7, nodeIndex.findNearby(flatModel.getNodes().get(0), 2.9).size());

		assertEquals(1, nodeIndex.findNearby(flatModel.getNodes().get(2), 0).size());
		assertEquals(1, nodeIndex.findNearby(flatModel.getNodes().get(2), 2.64).size());
		assertEquals(2, nodeIndex.findNearby(flatModel.getNodes().get(2), 2.65).size());
		assertEquals(7, nodeIndex.findNearby(flatModel.getNodes().get(2), 2.9).size());
	}
}
