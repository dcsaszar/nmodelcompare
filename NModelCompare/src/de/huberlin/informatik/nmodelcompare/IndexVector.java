package de.huberlin.informatik.nmodelcompare;

import com.savarese.spatial.GenericPoint;

public class IndexVector extends GenericPoint<Double>
{
	public IndexVector()
	{
		super(VectorDimension.DIMENSIONS);
		for (int d = 0; d < VectorDimension.DIMENSIONS; d++)
		{
			this.setCoord(d, 0.0);
		}
	}

	public void setCoord(VectorDimension d, int v)
	{
		setCoord(d.ordinal(), (double)v);
	}
}
