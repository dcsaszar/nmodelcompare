package de.huberlin.informatik.nmodelcompare.measurement;

import java.io.IOException;
import java.util.*;
import java.util.stream.*;

import org.jgrapht.alg.interfaces.MatchingAlgorithm.Matching;
import org.jgrapht.alg.matching.MaximumWeightBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;
import de.huberlin.informatik.nmodelcompare.Node;

public class ReproduceRubinPw
{
	public static class MergeModel
	{
		final String _modelId;
		final List<List<Node>> _mergedClasses;

		public MergeModel(List<Node> modelA)
		{
			_modelId = modelA.get(0).getModelId();
			_mergedClasses = modelA.stream().map(Arrays::asList).collect(Collectors.toList());
		}

		public MergeModel(MergeModel modelA, MergeModel modelB)
		{
			_modelId = _mergedModelId(modelA.getModelId(), modelB.getModelId());
			_mergedClasses = _compose(modelA.getMergedClasses(), modelB.getMergedClasses());
		}

		String getModelId()
		{
			return _modelId;
		}

		List<List<Node>> getMergedClasses()
		{
			return Collections.unmodifiableList(_mergedClasses);
		}
	}

	public static void main(String... args) throws IOException
	{
		List<String> inputs = Arrays.asList("hospitals","warehouses","random","randomLoose","randomTight");
		for (String input : inputs) {
			int chunkSize = input.contains("random") ? 10 : Integer.MAX_VALUE;
			List<NModelWorld> worlds = NModelWorldLoader.loadChunks(Option.CLASSES_ONLY, "testdata/"+input+".csv", chunkSize);
			double avgWeight = worlds.stream().mapToDouble(ReproduceRubinPw::getWeightOfMerge).sum() / worlds.size();
			System.out.printf("%11s PW weight: " + avgWeight + "\n", input);
		}
	}

	private static double getWeightOfMerge(NModelWorld world)
	{
		List<Node> allClassNodes = world.getNodes();
		int numberOfModels = world.getNumberOfInputModels();

		Collection<List<Node>> classesByModel = allClassNodes.stream().collect(Collectors.groupingBy(node -> node.getModelId())).values();
		List<MergeModel> modelPoolUnsorted = classesByModel.stream().map(MergeModel::new).collect(Collectors.toList());
		List<MergeModel> poolOfModels = _sortedPool(modelPoolUnsorted);

		IntStream.range(1, numberOfModels).forEach(iteration -> {
			MergeModel modelA = poolOfModels.get(0);
			MergeModel modelB = poolOfModels.get(1);
			MergeModel mergedModel = new MergeModel(modelA, modelB);
			poolOfModels.remove(modelA);
			poolOfModels.remove(modelB);
			poolOfModels.add(mergedModel);
			List<MergeModel> sortedPoolOfModels = _sortedPool(poolOfModels);
			poolOfModels.clear();
			poolOfModels.addAll(sortedPoolOfModels);
		});
		double weight = _getWeight(poolOfModels, world.getNumberOfInputModels());
		return weight;
	}

	private static double _getWeight(List<MergeModel> poolOfModels, int numberOfInputModels)
	{
		List<List<Node>> allTuples = poolOfModels.stream().flatMap(mergedModel -> mergedModel.getMergedClasses().stream())
				.collect(Collectors.toList());
		return new NwmWeight(allTuples, numberOfInputModels, false).sum();
	}

	private static List<MergeModel> _sortedPool(List<MergeModel> modelPoolUnsorted)
	{
		return modelPoolUnsorted.stream().sorted(Comparator.comparing(model -> model.getModelId())).collect(Collectors.toList());
	}

	private static List<List<Node>> _compose(List<List<Node>> classesA, List<List<Node>> classesB)
	{
		Matching<List<Node>, DefaultEdge> matching = getOptimalMatching(classesA, classesB);

		List<List<Node>> allMergedClasses = new ArrayList<>();
		Stream<List<Node>> nonMatched = Stream.concat(classesA.stream(), classesB.stream()).filter(node -> !matching.isMatched(node));
		nonMatched.forEach(n -> allMergedClasses.add(new ArrayList<>(n)));
		Set<DefaultEdge> edges = matching.getEdges();
		edges.forEach(edge -> {
			List<Node> mergedClassesA = matching.getGraph().getEdgeSource(edge);
			List<Node> mergedClassesB = matching.getGraph().getEdgeTarget(edge);
			List<Node> mergedClasses = Stream.concat(mergedClassesA.stream(), mergedClassesB.stream()).collect(Collectors.toList());
			allMergedClasses.add(mergedClasses);
		});
		return allMergedClasses;
	}

	private static String _mergedModelId(String idA, String idB)
	{
		return idA + idB;
	}

	private static Matching<List<Node>, DefaultEdge> getOptimalMatching(List<List<Node>> _classesA, List<List<Node>> _classesB)
	{
		SimpleWeightedGraph<List<Node>, DefaultEdge> g = new SimpleWeightedGraph<>(DefaultEdge.class);
		_classesA.stream().forEach(g::addVertex);
		_classesB.stream().forEach(g::addVertex);
		_classesA.stream().forEach(nodesA -> _classesB.stream().forEach(nodesB -> {
			List<Node> mergedClasses = Stream.concat(nodesA.stream(), nodesB.stream()).collect(Collectors.toList());
			double weight = NwmWeight.nonNormalizedWeightForTuple(mergedClasses, true);
			if (weight > 0) {
				DefaultEdge e = g.addEdge(nodesA, nodesB);
				g.setEdgeWeight(e, weight);
			}
		}));
		MaximumWeightBipartiteMatching<List<Node>, DefaultEdge> bipartiteMatching = new MaximumWeightBipartiteMatching<>(g,
				new HashSet<>(_classesA),
				new HashSet<>(_classesB));
		return bipartiteMatching.getMatching();
	}
}
