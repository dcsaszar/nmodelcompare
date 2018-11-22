package de.huberlin.informatik.nmodelcompare;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

public class FlatModel
{
	private List<Node> _nodes;
	private EPackage _model;
	private Integer _id;

	public FlatModel(EPackageImpl model) {
		this(model, null);
	}

	public FlatModel(EPackage model, Integer id)
	{
		_id = id;
		_nodes = new ArrayList<>();
		_model = model;
		model.eAllContents().forEachRemaining(o -> {
			String className = o.eClass().getName();
			switch (className)
			{
			case "EClass":
			case "EReference":
			case "EAttribute":
			case "EOperation":
				_nodes.add(new Node(this, o));
				break;
			case "EAnnotation":
			case "EGenericType":
			case "EStringToStringMapEntry":
				break;
			default:
				throw new Error("Unknown class " + className);
			}
		});
	}

	public List<Node> getNodes()
	{
		return _nodes;
	}

	public String getName()
	{
		return _model.getName();
	}

	public Integer getId()
	{
		return _id;
	}
}
