package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimilaritiesTest
{
	@Test
	void similarityOfTwoModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		assertEquals(13, world.getNodes().size());
		assertEquals(13, similarities.get2DWidth());
		assertEquals(0, similarities.getDistance(7, 7), 0.0001);
		assertEquals(1.4, similarities.getDistance(9, 7), 0.1);
		assertEquals(1.4, similarities.getDistance(7, 9), 0.1);

		int nullCount = 0;
		double sum = 0;
		for (int i = 0; i < 13; i++) {
			for (int j = 0; j < 13; j++) {
				if (similarities.getDistance(i, j) == null) {
					nullCount++;
				}
				else {
					sum += similarities.getDistance(i, j);
				}
			}
		}
		assertEquals(73.5, sum, 0.1);
		assertEquals(100, nullCount);
	}

	@Test
	void normalizedDistance()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore");
		Similarities similarities = world.findSimilarities(2.7);
		assertEquals(0, similarities.getNormalizedDistance(11, 11), 0.0001);
		assertEquals(0, similarities.getNormalizedDistance(1, 8), 0.0001);
		assertEquals(0.54, similarities.getNormalizedDistance(7, 9), 0.01);
	}

	@Test
	void similarityOfThreeModels()
	{
		NModelWorld world = NModelWorldLoader.loadEcore("testdata/many_words.ecore", "testdata/few_words.ecore", "testdata/full_sample.ecore");
		Similarities similarities = world.findSimilarities(2.5);
		assertEquals(21, world.getNodes().size());
		assertEquals(21, similarities.get2DWidth());

		int nullCount = 0;
		double sum = 0;
		for (int i = 0; i < similarities.get2DWidth(); i++) {
			for (int j = 0; j < similarities.get2DWidth(); j++) {
				if (similarities.getDistance(i, j) == null) {
					nullCount++;
				}
				else {
					sum += similarities.getDistance(i, j);
				}
			}
		}
		assertEquals(81.5, sum, 0.1);
		assertEquals(360, nullCount);
	}
}
