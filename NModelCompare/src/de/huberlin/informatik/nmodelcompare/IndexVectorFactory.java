package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IndexVectorFactory
{
	private Map<String, Integer> _nameDimension;
	private Map<String, Integer> _nameSubstringDimension;
	private final int _dimensions;

	public IndexVectorFactory(List<Node> nodes)
	{
		fillLexicalIndex(nodes);
		_dimensions = VectorDimension.DIMENSIONS + _nameDimension.size() + _nameSubstringDimension.size();
	}

	private void fillLexicalIndex(List<Node> nodes)
	{
		Set<String> names = nodes.stream().map((node) -> node.getName()).collect(Collectors.toSet());
		Set<String> nameSubstrings = names.stream().map(IndexVectorFactory::substrings).flatMap(Collection::stream).collect(Collectors.toSet());
		AtomicInteger i = new AtomicInteger(VectorDimension.DIMENSIONS);
		_nameDimension = names.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
		_nameSubstringDimension = nameSubstrings.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
	}

	private static Pattern PASCAL_OR_SNAKE = Pattern.compile("(([_A-Z]|^)[^_A-Z]+)|(([_A-Z]|^)+(?=$|[_A-Z][^_A-Z]))");

	private static List<String> substrings(String name)
	{
		List<String> allMatches = new ArrayList<String>();
		Matcher m = PASCAL_OR_SNAKE.matcher(name);
		while (m.find())
		{
			allMatches.add(m.group());
		}
		return allMatches.size() == 1 ? Arrays.asList(new String[0]) : allMatches;
	}

	IndexVector vectorFor(Node node)
	{
		IndexVector v = new IndexVector(_dimensions);
		v.setCoord(VectorDimension.LENGTH_OF_NAME, node.getName().length());
		v.setCoord(VectorDimension.NUMBER_OF_ATTRIBUTES, node.getNumberOfAttributes());
		v.setCoord(VectorDimension.NUMBER_OF_METHODS, node.getNumberOfMethods());
		v.setCoord(VectorDimension.NUMBER_OF_REFERENCES, node.getNumberOfReferences());

		v.setCoord(_nameDimension.get(node.getName()), 1);
		substrings(node.getName()).stream().forEach(s -> v.setCoord(_nameSubstringDimension.get(s), 1));

		return v;
	}
}
