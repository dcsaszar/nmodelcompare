package de.huberlin.informatik.nmodelcompare;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENamedElementImpl;

public class Node
{
	private EObject _eObject;
	private FlatModel _flatModel;

	public Node(FlatModel flatModel, EObject eObject)
	{
		_flatModel = flatModel;
		_eObject = eObject;
	}

	public EObject getEObject()
	{
		return _eObject;
	}

	public String getName()
	{
		return ((ENamedElementImpl)_eObject).getName();
	}

	public String getType()
	{
		return _eObject.eClass().getName();
	}

	public int getNumberOfAttributes()
	{
		return _eObject.eContents().stream().mapToInt(o -> o.eClass().getName() == "EAttribute" ? 1 : 0).sum();
	}
}
