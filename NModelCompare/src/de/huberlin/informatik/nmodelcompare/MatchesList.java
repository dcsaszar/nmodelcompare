package de.huberlin.informatik.nmodelcompare;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MatchesList
{
	private List<List<Node>> _matches;

	public MatchesList(Set<Set<Node>> matches)
	{
		_matches = matches.stream()
				.map(set -> set.stream().sorted((a, b) -> a.getDescription().compareTo(b.getDescription())).collect(Collectors.toList()))
				.sorted((a, b) -> a.get(0).getDescription().compareTo(b.get(0).getDescription())).collect(Collectors.toList());
	}

	public List<List<Node>> getAll()
	{
		return _matches;
	}

	public String getDescription()
	{
		return getAll().stream().map(match -> describe(match)).collect(Collectors.joining("\n"));
	}

	private String describe(List<Node> match)
	{
		return IntStream.range(0, match.size()).mapToObj(i -> {
			String description = match.get(i).getDescription();
			if (i == 0) {
				return description;
			}
			String prev = match.get(i - 1).getFullNameTyped();
			return description.startsWith(prev) ? description.substring(prev.length()) : description;
		}).collect(Collectors.joining(","));
	}
}
