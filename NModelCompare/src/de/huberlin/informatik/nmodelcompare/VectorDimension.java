package de.huberlin.informatik.nmodelcompare;

public enum VectorDimension
{
	INV_LENGTH_OF_NAME, INV_NUMBER_OF_ATTRIBUTES, INV_NUMBER_OF_METHODS, INV_NUMBER_OF_REFERENCES, IS_CLASS, IS_ATTRIBUTE, IS_OPERATION, IS_REFERENCE;

	public final static int DIMENSIONS = VectorDimension.values().length;
}
