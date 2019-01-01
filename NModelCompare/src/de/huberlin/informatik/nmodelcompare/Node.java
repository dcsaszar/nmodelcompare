package de.huberlin.informatik.nmodelcompare;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.ENamedElementImpl;

public class Node
{
	private EObject _eObject;
	private String _modelId;

	public Node(FlatModel flatModel, EObject eObject)
	{
		this(String.valueOf(flatModel.getId()), eObject);
	}

	public Node(String modelId, EObject eObject)
	{
		_modelId = modelId;
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

	public String getModelId()
	{
		return _modelId;
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
		return _modelId == otherNode.getModelId();
	}

	public String getParentName()
	{
		EObject eContainer = _eObject.eContainer();
		return (eContainer instanceof EPackage || eContainer == null) ? null : ((ENamedElementImpl)eContainer).getName();
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
