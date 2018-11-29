package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NwmWeight
{
	private final Set<Set<Node>> _classMatches;
	private final int _numberOfInputModels; // n

	public NwmWeight(Set<Set<Node>> matchesSet, int numberOfInputModels)
	{
		_numberOfInputModels = numberOfInputModels;
		_classMatches = matchesSet.stream()
				.map(match -> match.stream().filter(node -> node.getType() == "EClass").collect(Collectors.toSet())).filter(s -> s.size() > 1)
				.collect(Collectors.toSet());
	}

	public double sum()
	{
		return _classMatches.stream().mapToDouble(match -> getWeightForTuple(match)).sum();
	}

	private double getWeightForTuple(Set<Node> match)
	{
		List<Set<String>> matchProperties = match.stream()
				.map(node -> Stream.concat(Arrays.asList(node.getName()).stream(), node.getChildrenNames().stream()).collect(Collectors.toSet()))
				.collect(Collectors.toList()); // pi(e1) .. pi(em)
		Set<String> allDistinctProperties = matchProperties.stream().flatMap(p -> p.stream()).distinct()
				.collect(Collectors.toSet()); // pi(t)
		long nominator = allDistinctProperties.stream().mapToLong(p -> {
			long numberOfElementsWithP = matchProperties.stream().filter(properties -> properties.contains(p)).count(); // j
			if (numberOfElementsWithP < 2) {
				return 0;
			}
			return numberOfElementsWithP * numberOfElementsWithP;
		}).sum();
		long numberOfDistictProperties = allDistinctProperties.size(); // |pi(t)|
		return ((double)nominator) / (_numberOfInputModels * _numberOfInputModels * numberOfDistictProperties);
	}
}
