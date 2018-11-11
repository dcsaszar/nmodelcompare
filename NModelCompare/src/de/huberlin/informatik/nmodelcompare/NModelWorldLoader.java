package de.huberlin.informatik.nmodelcompare;

import java.util.ArrayList;
import java.util.List;

public class NModelWorldLoader
{
	public static NModelWorld loadEcore(String... filenames)
	{
		List<Node> nodes = new ArrayList<Node>();
		int i = 1;
		for (String filename : filenames) {
			FlatModel flatModel = new FlatModel(ModelLoader.loadEcore(filename), i);
			nodes.addAll(flatModel.getNodes());
			i++;
		}
		NModelWorld result = new NModelWorld(nodes);
		return result;
	}
}
