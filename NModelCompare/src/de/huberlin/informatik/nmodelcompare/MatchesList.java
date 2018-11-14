package de.huberlin.informatik.nmodelcompare;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		String name0 = match.get(0).getFullNameTyped();
		List<String> descriptions = match.stream()
				.map(n -> n.getDescription().startsWith(name0) ? n.getDescription().replaceFirst(name0, "") : n.getDescription())
				.collect(Collectors.toList());
		descriptions.set(0, match.get(0).getDescription());
		return String.join(",", descriptions);
	}
}
