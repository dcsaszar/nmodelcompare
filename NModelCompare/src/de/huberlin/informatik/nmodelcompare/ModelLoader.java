package de.huberlin.informatik.nmodelcompare;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.*;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcoreFactoryImpl;
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

	public static List<EPackage> loadCsv(String path) throws IOException
	{
		CSVParser parser = CSVParser.parse(new File(path), Charset.forName("utf-8"), CSVFormat.DEFAULT.withRecordSeparator('\n'));
		Map<String, List<CSVRecord>> csvModels = parser.getRecords().stream().collect(Collectors.groupingBy(record -> record.get(0).toString()));
		return csvModels.values().stream().map(csvRecords -> eCoreModelFromCsv(csvRecords)).collect(Collectors.toList());
	}
	
	private static EPackage eCoreModelFromCsv(List<CSVRecord> csvRecords)
	{
		EcoreFactoryImpl factory = new EcoreFactoryImpl();
		EPackage ePackage = factory.createEPackage();
		ePackage.setName(csvRecords.get(0).get(0).toString());
		csvRecords.stream().forEach(record -> {
			EClass eClass = factory.createEClass();
			eClass.setName(record.get(1));
			ePackage.getEClassifiers().add(eClass);
			String names = record.get(2);
			for (String name : names.split(";")) {
				EAttribute eAttribute = factory.createEAttribute();
				eAttribute.setName(name);
				eClass.getEStructuralFeatures().add(eAttribute);
			}
		});
		return ePackage;
	}
}
