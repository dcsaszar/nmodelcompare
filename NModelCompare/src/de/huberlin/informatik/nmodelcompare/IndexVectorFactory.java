package de.huberlin.informatik.nmodelcompare;

public class IndexVectorFactory
{
	IndexVector vectorFor(Node node)
	{
		IndexVector v = new IndexVector();
		v.setCoord(VectorDimension.LON, node.getName().length());
		v.setCoord(VectorDimension.NOA, node.getNumberOfAttributes());
		return v;
	}
}
