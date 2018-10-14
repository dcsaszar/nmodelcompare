package de.huberlin.informatik.nmodelcompare;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.impl.EPackageImpl;

public class FlatModel
{
	private List<Node> _nodes;

	public FlatModel(EPackageImpl model)
	{
		_nodes = new ArrayList<>();
		model.eAllContents().forEachRemaining(o -> {
			switch (o.eClass().getName())
			{
			case "EClass":
			case "EReference":
			case "EAttribute":
				_nodes.add(new Node(this, o));
				break;
			case "EGenericType":
				break;
			default:
				throw new Error("unknown");
			}
		});
	}

	public List<Node> getNodes()
	{
		return _nodes;
	}
}
