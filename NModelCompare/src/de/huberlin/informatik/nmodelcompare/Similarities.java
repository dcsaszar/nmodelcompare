package de.huberlin.informatik.nmodelcompare;

import java.util.HashMap;

import org.javatuples.Pair;

public class Similarities
{
	private HashMap<Pair<Integer, Integer>, Double> _matrix;
	private int _width;
	private Double _maxDistance;

	public Similarities(int width)
	{
		_maxDistance = 0d;
		_matrix = new HashMap<Pair<Integer, Integer>, Double>();
		_width = width;
	}

	public void addDistance(int nodeIndexX, int nodeIndexY, double distance)
	{
		_maxDistance = Math.max(distance, _maxDistance);
		_matrix.put(new Pair<>(nodeIndexX, nodeIndexY), distance);
		_matrix.put(new Pair<>(nodeIndexY, nodeIndexX), distance);
	}

	public int get2DWidth()
	{
		return _width;
	}

	public Double getDistance(int nodeIndexX, int nodeIndexY)
	{
		return _matrix.get(new Pair<>(nodeIndexX, nodeIndexY));
	}

	public Double getNormalizedDistance(int nodeIndexX, int nodeIndexY)
	{
		Double d = getDistance(nodeIndexX, nodeIndexY);
		if (d == null) {
			return null;
		}
		if (_maxDistance == 0) {
			return 1d;
		}
		return d / _maxDistance;
	}
}
