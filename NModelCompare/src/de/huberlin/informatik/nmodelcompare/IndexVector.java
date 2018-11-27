package de.huberlin.informatik.nmodelcompare;

import com.savarese.spatial.GenericPoint;

public class IndexVector extends GenericPoint<Double>
{
	public IndexVector(int dimensions)
	{
		super(dimensions);
		for (int d = 0; d < dimensions; d++)
		{
			this.setCoord(d, 0.0);
		}
	}

	public void setCoord(VectorDimension d, int v)
	{
		setCoord(d.ordinal(), (double)v);
	}

	public void setCoord(VectorDimension d, double v)
	{
		super.setCoord(d.ordinal(), (double)v);
	}

	public void setCoord(int d, int v)
	{
		super.setCoord(d, (double)v);
	}
}
