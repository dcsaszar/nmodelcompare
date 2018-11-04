package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.IntStream;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.junit.jupiter.api.Test;

class IndexVectorTest
{
	private static final double E = 0.000000001;
	private static final EPackageImpl model = ModelLoader.loadEcore("testdata/full_sample.ecore");

	@Test
	void containsMetricalIndexes()
	{
		FlatModel flatModel = new FlatModel(model);
		IndexVectorFactory indexVectorFactory = new IndexVectorFactory(flatModel.getNodes());
		IndexVector indexVector = indexVectorFactory.vectorFor(flatModel.getNodes().get(0));
		assertEquals("MyTestClass".length(), indexVector.getCoord(VectorDimension.LENGTH_OF_NAME.ordinal()), E);
		assertEquals(3, indexVector.getCoord(VectorDimension.NUMBER_OF_ATTRIBUTES.ordinal()), E);
		assertEquals(2, indexVector.getCoord(VectorDimension.NUMBER_OF_METHODS.ordinal()), E);
		assertEquals(1, indexVector.getCoord(VectorDimension.NUMBER_OF_REFERENCES.ordinal()), E);
	}

	@Test
	void containsClassificationIndexes()
	{
		FlatModel flatModel = new FlatModel(model);
		IndexVectorFactory indexVectorFactory = new IndexVectorFactory(flatModel.getNodes());
		IndexVector indexVector = indexVectorFactory.vectorFor(flatModel.getNodes().get(0));
		assertEquals("MyTestClass".length(), indexVector.getCoord(VectorDimension.LENGTH_OF_NAME.ordinal()), E);
		assertEquals(1, indexVector.getCoord(VectorDimension.IS_CLASS.ordinal()), E);
		assertEquals(0, indexVector.getCoord(VectorDimension.IS_ATTRIBUTE.ordinal()), E);
		assertEquals(0, indexVector.getCoord(VectorDimension.IS_OPERATION.ordinal()), E);
		assertEquals(0, indexVector.getCoord(VectorDimension.IS_REFERENCE.ordinal()), E);
	}

	@Test
	void containsLexicalIndexes()
	{
		FlatModel flatModel = new FlatModel(model);
		IndexVectorFactory indexVectorFactory = new IndexVectorFactory(flatModel.getNodes());
		IndexVector indexVector = indexVectorFactory.vectorFor(flatModel.getNodes().get(0));
		int d = indexVector.getDimensions();

		assertEquals(26, d - VectorDimension.DIMENSIONS);

		double lexicalSum = IntStream.range(VectorDimension.DIMENSIONS, d).mapToDouble(i -> indexVector.getCoord(i)).sum();
		assertEquals(5, lexicalSum, E);

		IndexVector indexVector1 = indexVectorFactory.vectorFor(flatModel.getNodes().get(1));
		lexicalSum = IntStream.range(VectorDimension.DIMENSIONS, d).mapToDouble(i -> indexVector1.getCoord(i)).sum();
		assertEquals(4, lexicalSum, E);

		IndexVector indexVector2 = indexVectorFactory.vectorFor(flatModel.getNodes().get(2));
		lexicalSum = IntStream.range(VectorDimension.DIMENSIONS, d).mapToDouble(i -> indexVector2.getCoord(i)).sum();
		assertEquals(4, lexicalSum, E);
	}
}
