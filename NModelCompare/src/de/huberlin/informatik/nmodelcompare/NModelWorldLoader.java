package de.huberlin.informatik.nmodelcompare;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;

public class NModelWorldLoader
{
	public enum Option
	{
		ALL, CLASSES_ONLY
	}

	private Option _option;

	public NModelWorldLoader(Option option)
	{
		_option = option;
	}

	public static NModelWorld load(Option option, String... filenames) throws IOException
	{
		if (filenames.length == 1) {
			return new NModelWorldLoader(option).loadCsv(filenames[0]);
		}
		return new NModelWorldLoader(option).loadEcoreModels(filenames);
	}

	public static NModelWorld load(String... filenames) throws IOException
	{
		return load(Option.ALL, filenames);
	}

	public static NModelWorld loadEcore(String... filenames)
	{
		try {
			return load(filenames);
		}
		catch (IOException e) {
			throw new Error(e);
		}
	}

	private NModelWorld loadEcoreModels(String[] filenames)
	{
		return fillNModelWorld(Arrays.stream(filenames).map(path -> ModelLoader.loadEcore(path)).collect(Collectors.toList()));
	}

	private NModelWorld loadCsv(String filename) throws IOException
	{
		return fillNModelWorld(ModelLoader.loadCsv(filename));
	}

	private NModelWorld fillNModelWorld(List<EPackage> ecoreModels)
	{
		List<Node> nodes = new ArrayList<Node>();
		int i = 1;
		for (EPackage model : ecoreModels) {
			FlatModel flatModel = new FlatModel(model, i);
			List<Node> flatModelNodes = flatModel.getNodes();
			if (_option == Option.CLASSES_ONLY) {
				flatModelNodes = flatModelNodes.stream().filter(node -> node.getType().equals("EClass")).collect(Collectors.toList());
			}
			nodes.addAll(flatModelNodes);
			i++;
		}
		NModelWorld result = new NModelWorld(nodes, ecoreModels.size());
		return result;
	}
}
