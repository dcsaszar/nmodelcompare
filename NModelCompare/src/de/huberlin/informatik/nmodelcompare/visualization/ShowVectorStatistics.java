package de.huberlin.informatik.nmodelcompare.visualization;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

public class ShowVectorStatistics
{
	final static List<String> TEST_CASES = Arrays.asList("captbaritone_webamp", "jeffersonRibeiro_react-shopping-cart", "kabirbaidhya_react-todo-app",
			"hospitals", "warehouses", "random", "randomLoose", "randomTight");

	final static Option option = Option.CLASSES_ONLY;

	public static void main(String... args) throws IOException
	{
		for (int i = 0; i <= 6; i++) {
			System.out.print("TestCase K ParentName Name NameSubstring ChildrenNames NameSubstringList:".split(" ")[i] + "\t");
			for (String testCase : TEST_CASES) {
				if (i == 0) {
					System.out.print(testCase + "\t");
					continue;
				}

				boolean useAverage = testCase.startsWith("random");
				int chunksCount = useAverage ? 10 : 1;
				int chunkSize = chunksCount == 1 ? Integer.MAX_VALUE : 10;

				List<NModelWorld> worlds;
				try {
					worlds = NModelWorldLoader.loadChunks(option, "testdata/" + testCase + ".csv", chunkSize);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}

				if (i == 6) {
					IndexVectorFactory indexVectorFactory = new IndexVectorFactory(worlds.get(0).getNodes());
					ArrayList<String> nameSubstrings = new ArrayList<String>(indexVectorFactory.getNameSubstringDimension().keySet());
					ArrayList<String> names = new ArrayList<String>(indexVectorFactory.getNameDimension().keySet());
					ArrayList<String> childrenNames = new ArrayList<String>(indexVectorFactory.getChildrenNamesDimension().keySet());
					ArrayList<String> parentNames = new ArrayList<String>(indexVectorFactory.getParentNameDimension().keySet());
					nameSubstrings.sort(Comparator.naturalOrder());
					names.sort(Comparator.naturalOrder());
					childrenNames.sort(Comparator.naturalOrder());
					parentNames.sort(Comparator.naturalOrder());
					System.out.println("\n" + testCase + ":" + "\nPARENT_NAME: " + parentNames + "\nNAME: " + names + "\nNAME_SUBSTRING: " + nameSubstrings
							+ "\nCHILDREN_NAMES: " + childrenNames);
					continue;
				}

				final int j = i;
				double avg = worlds.stream().mapToDouble(world -> {
					IndexVectorFactory indexVectorFactory = new IndexVectorFactory(world.getNodes());
					String data = indexVectorFactory.getDescription();
					String value = data.split("\t")[j - 1];
					return Integer.parseInt(value);
				}).average().getAsDouble();
				DecimalFormat format = new DecimalFormat("0.###");
				System.out.printf(format.format(avg) + "\t");
			}
			System.out.println();
		}
	}
}
