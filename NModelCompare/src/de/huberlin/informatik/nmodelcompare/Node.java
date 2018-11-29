package de.huberlin.informatik.nmodelcompare;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.*;
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

	public int getNumberOfMethods()
	{
		return _eObject.eContents().stream().mapToInt(o -> o.eClass().getName() == "EOperation" ? 1 : 0).sum();
	}

	public int getNumberOfReferences()
	{
		return _eObject.eContents().stream().mapToInt(o -> o.eClass().getName() == "EReference" ? 1 : 0).sum();
	}

	public String getDescription()
	{
		return getFullNameTyped() + "-M" + getModelId();
	}

	public Integer getModelId()
	{
		return _flatModel.getId();
	}

	public String getFullName()
	{
		return getParentName() == null ? getName() : (getParentName() + "." + getName());
	}

	public String getFullNameTyped()
	{
		return getFullName() + getType().substring(1, 2).replace("C", "");
	}

	public boolean isInSameModel(Node otherNode)
	{
		return _flatModel == otherNode._flatModel;
	}

	public String getParentName()
	{
		EObject eContainer = _eObject.eContainer();
		return (eContainer instanceof EPackage) ? null : ((ENamedElementImpl)eContainer).getName();
	}

	public List<String> getChildrenNames()
	{
		return _eObject.eContents().stream().filter(o -> o instanceof ENamedElement).map(o -> ((ENamedElement)o).getName())
				.collect(Collectors.toList());
	}

	@Override
	public String toString()
	{
		return getDescription();
	}
}
