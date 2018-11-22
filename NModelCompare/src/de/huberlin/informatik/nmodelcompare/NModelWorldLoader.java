package de.huberlin.informatik.nmodelcompare;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;

public class NModelWorldLoader
{
	public static NModelWorld load(String... filenames) throws IOException
	{
		if (filenames.length == 1) {
			return loadCsv(filenames[0]);
		}
		return loadEcore(filenames);
	}

	public static NModelWorld loadEcore(String... filenames)
	{
		return fillNModelWorld(Arrays.stream(filenames).map(path -> ModelLoader.loadEcore(path)).collect(Collectors.toList()));
	}

	public static NModelWorld loadCsv(String filename) throws IOException
	{
		return fillNModelWorld(ModelLoader.loadCsv(filename));
	}

	public static NModelWorld fillNModelWorld(List<EPackage> ecoreModels)
	{
		List<Node> nodes = new ArrayList<Node>();
		int i = 1;
		for (EPackage model : ecoreModels) {
			FlatModel flatModel = new FlatModel(model, i);
			nodes.addAll(flatModel.getNodes());
			i++;
		}
		NModelWorld result = new NModelWorld(nodes);
		return result;
	}
}
