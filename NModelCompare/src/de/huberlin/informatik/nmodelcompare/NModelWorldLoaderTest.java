package de.huberlin.informatik.nmodelcompare;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

class NModelWorldLoaderTest
{
	@Test
	void canLoadChunks() throws IOException
	{
		List<NModelWorld> worlds = NModelWorldLoader.loadChunks(Option.CLASSES_ONLY, "testdata/random.csv", 10);
		assertEquals(10, worlds.size());
		assertEquals(10, worlds.get(0).getNumberOfInputModels());
		assertEquals(271, worlds.get(0).getNodes().size());
		assertEquals(10, worlds.get(5).getNumberOfInputModels());
		assertEquals(286, worlds.get(5).getNodes().size());
		assertEquals(10, worlds.get(9).getNumberOfInputModels());
		assertEquals(302, worlds.get(9).getNodes().size());
	}

	@Test
	void elementsHaveAnOrder() throws IOException
	{
		List<NModelWorld> worlds10 = NModelWorldLoader.loadChunks(Option.CLASSES_ONLY, "testdata/random.csv", 10);
		List<NModelWorld> worlds50 = NModelWorldLoader.loadChunks(Option.CLASSES_ONLY, "testdata/random.csv", 50);
		List<Node> nodes10 = worlds10.get(5).getNodes();
		List<Node> nodes50 = worlds50.get(1).getNodes().subList(0, 286);
		Object[] names10 = nodes10.stream().map(Node::getName).collect(Collectors.toList()).toArray();
		Object[] names50 = nodes50.stream().map(Node::getName).collect(Collectors.toList()).toArray();
		assertArrayEquals(names50, names10);
	}
}
