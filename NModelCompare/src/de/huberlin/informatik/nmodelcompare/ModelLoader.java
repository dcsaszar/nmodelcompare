package de.huberlin.informatik.nmodelcompare;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class ModelLoader
{
	public static EPackageImpl loadEcore(String path)
	{
		ResourceSet resSet = new ResourceSetImpl();
		resSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(FilenameUtils.getExtension(path), new XMIResourceFactoryImpl());

		Resource resource = resSet.getResource(URI.createURI(path), true);
		EObject root = resource.getContents().get(0);

		return (EPackageImpl)root;
	}
}
