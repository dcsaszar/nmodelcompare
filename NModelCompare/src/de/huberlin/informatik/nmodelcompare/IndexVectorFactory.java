package de.huberlin.informatik.nmodelcompare;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IndexVectorFactory
{
	private Map<String, Integer> _parentNameDimension;
	private Map<String, Integer> _nameDimension;
	private Map<String, Integer> _nameSubstringDimension;
	private Map<String, Integer> _childrenNamesDimension;
	private final int _dimensions;

	public IndexVectorFactory(List<Node> nodes)
	{
		_dimensions = fillLexicalIndex(nodes);
	}

	private int fillLexicalIndex(List<Node> nodes)
	{
		Set<String> parentNames = nodes.stream().map(Node::getParentName).collect(Collectors.toSet());
		Set<String> names = nodes.stream().map(Node::getName).collect(Collectors.toSet());
		Set<String> childrenNames = nodes.stream().flatMap(node -> node.getChildrenNames().stream()).collect(Collectors.toSet());
		Set<String> nameSubstrings = names.stream().map(IndexVectorFactory::substrings).flatMap(Collection::stream).collect(Collectors.toSet());
		AtomicInteger i = new AtomicInteger(VectorDimension.DIMENSIONS);
		_parentNameDimension = parentNames.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
		_nameDimension = names.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
		_nameSubstringDimension = nameSubstrings.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
		_childrenNamesDimension = childrenNames.stream().collect(Collectors.toMap(s -> s, s -> i.getAndIncrement()));
		return i.get();
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

		v.setCoord(VectorDimension.INV_LENGTH_OF_NAME, 1d / node.getName().length());

		v.setCoord(VectorDimension.INV_NUMBER_OF_ATTRIBUTES, 1d / (1 + node.getNumberOfAttributes()));
		v.setCoord(VectorDimension.INV_NUMBER_OF_METHODS, 1d / (1 + node.getNumberOfMethods()));
		v.setCoord(VectorDimension.INV_NUMBER_OF_REFERENCES, 1d / (1 + node.getNumberOfReferences()));

		v.setCoord(VectorDimension.IS_ATTRIBUTE, node.getType() == "EAttribute" ? 1 : 0);
		v.setCoord(VectorDimension.IS_CLASS, node.getType() == "EClass" ? 1 : 0);
		v.setCoord(VectorDimension.IS_OPERATION, node.getType() == "EOperation" ? 1 : 0);
		v.setCoord(VectorDimension.IS_REFERENCE, node.getType() == "EReference" ? 1 : 0);

		v.setCoord(_parentNameDimension.get(node.getParentName()), 1);
		v.setCoord(_nameDimension.get(node.getName()), 1);
		substrings(node.getName()).stream().forEach(s -> v.setCoord(getNameSubstringDimension().get(s), 1));
		node.getChildrenNames().stream().forEach(n -> v.setCoord(_childrenNamesDimension.get(n), 1));

		return v;
	}

	public int getDimensions()
	{
		return _dimensions;
	}

	public String getDescription()
	{
		return "" + _dimensions + "\t" + _parentNameDimension.size() + "\t" + _nameDimension.size() + "\t" + getNameSubstringDimension().size() + "\t"
				+ _childrenNamesDimension.size();
	}

	public Map<String, Integer> getNameSubstringDimension()
	{
		return _nameSubstringDimension;
	}

	public Map<String, Integer> getChildrenNamesDimension()
	{
		return _childrenNamesDimension;
	}

	public Map<String, Integer> getNameDimension()
	{
		return _nameDimension;
	}

	public Map<String, Integer> getParentNameDimension()
	{
		return _parentNameDimension;
	}
}
