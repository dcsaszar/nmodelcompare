package de.huberlin.informatik.nmodelcompare.visualization;

import java.io.IOException;

import org.javatuples.Pair;
import org.ujmp.core.Matrix;
import org.ujmp.core.genericmatrix.impl.DefaultDenseGenericMatrix2D;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

public class VisualizeSimilarityMatrix
{
	public static void main(String... args) throws IOException
	{
		NModelWorld world = NModelWorldLoader.load(args);
		if (world.getNodes().size() == 0) {
			System.out.println("Using fallback dataset.");
			world = NModelWorldLoader.load(Option.CLASSES_ONLY, "testdata/random_subset.csv");
			// world =
			// NModelWorldLoader.loadEcore("testdata/react_todo_app_2017021115_Akasky70_react_todo_app_step_10_c15f550b.ecore",
			// "testdata/react_todo_app_2017021519_Akasky70_react_todo_app_step_15_4fe6b982.ecore",
			// "testdata/react_todo_app_2017062714_master_c9ef612a.ecore");
		}
		Similarities allSimilarities = world.findSimilarities(5);

		AbstractMatches idMatches = new IdMatches(allSimilarities);
		AbstractMatches greedyDistanceMatches = new GreedyDistanceMatches(allSimilarities);

		Matrix idMatrix = generateMatrix(allSimilarities, idMatches);
		Matrix greedyDistanceMatrix = generateMatrix(allSimilarities, greedyDistanceMatches);
		greedyDistanceMatrix.setMetaData("IdMatches", idMatrix);

		greedyDistanceMatrix.showGUI();
	}

	private static Matrix generateMatrix(Similarities allSimilarities, AbstractMatches matches)
	{
		String matchesDescription = matches.getMatchesList().getDescription();

		Similarities similarities = matches.getRemaining();

		Matrix matrix = new DefaultDenseGenericMatrix2D<>(allSimilarities.get2DWidth(), allSimilarities.get2DWidth());
		matrix.setMetaData("matchesDescription", matchesDescription);
		for (int i = 0; i < allSimilarities.get2DWidth(); i++) {
			Node nodeI = allSimilarities.getNodes().get(i);
			matrix.setRowLabel(i, nodeI.getDescription());
			matrix.setColumnLabel(i, nodeI.getDescription());
			for (int j = 0; j < allSimilarities.get2DWidth(); j++) {
				Node nodeJ = allSimilarities.getNodes().get(j);
				matrix.setAsString("", i, j);
				if (new Double(0).equals(allSimilarities.getDistance(new Pair<>(nodeI, nodeJ)))) {
					matrix.setAsString(".", i, j);
				}
				if (similarities.getDistance(new Pair<>(nodeI, nodeJ)) != null) {
					Double d = similarities.getNormalizedDistance(new Pair<>(nodeI, nodeJ));
					if (d == 0) {
						matrix.setAsString(nodeI.getFullName(), i, j);
					}
					else {
						matrix.setAsDouble(((1 - d) * (nodeI.isInSameModel(nodeJ) ? -1 : 1)), i, j);
					}
				}
			}
		}
		return matrix;
	}
}
