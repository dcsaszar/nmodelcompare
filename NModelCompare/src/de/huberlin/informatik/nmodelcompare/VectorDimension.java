package de.huberlin.informatik.nmodelcompare;

public enum VectorDimension
{
	LENGTH_OF_NAME, NUMBER_OF_ATTRIBUTES, NUMBER_OF_METHODS, NUMBER_OF_REFERENCES, IS_CLASS, IS_ATTRIBUTE, IS_OPERATION, IS_REFERENCE;

	public final static int DIMENSIONS = VectorDimension.values().length;
}
