package de.huberlin.informatik.nmodelcompare.measurement;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

public class MeasureNwmWeight
{
	final static List<String> TEST_CASES = Arrays.asList("hospitals", "warehouses", "random", "randomLoose", "randomTight");
	final static List<Double> RADII = Arrays.asList(0.0, 2.5, 2.75, 3.0, 3.125, 3.25, 3.5, 3.75, 4.0, 4.25, 4.5, 4.75, 5.0, 5.5, 6.5, 7.5);
	final static Option option = Option.CLASSES_ONLY;

	public static void main(String... args) throws IOException
	{
		warmUp();

		for (double radius : RADII) {
			for (String testCase : TEST_CASES) {
				int chunkSize = testCase.startsWith("random") ? 10 : Integer.MAX_VALUE;

				Instant startedLoadingAt = Instant.now();
				List<NModelWorld> worlds = NModelWorldLoader.loadChunks(option, "testdata/" + testCase + ".csv", chunkSize);
				Instant loadedAt = Instant.now();

				long loadTimeElapsedMillis = Duration.between(startedLoadingAt, loadedAt).toMillis();
				int modelCount = worlds.parallelStream().mapToInt(NModelWorld::getNumberOfInputModels).sum();
				System.out.print("Using " + testCase + "; r=" + radius + "; " + option + " " + modelCount + "/" + worlds.size() + "");
				long searchTimeElapsedMillis = 0;
				long matchTimeElapsedMillis = 0;
				double nwmWeight = 0;
				for (NModelWorld world : worlds) {
					if (worlds.size() > 1) {
						System.out.print(".");
					}
					modelCount += world.getNumberOfInputModels();
					Instant startedAt = Instant.now();
					Similarities allSimilarities = world.findSimilarities(radius);
					Instant foundSimilaritiesAt = Instant.now();
					AbstractMatches matches = new WeightOptimizedMatches(allSimilarities);
					Instant finishedAt = Instant.now();

					searchTimeElapsedMillis += Duration.between(startedAt, foundSimilaritiesAt).toMillis();
					matchTimeElapsedMillis += Duration.between(foundSimilaritiesAt, finishedAt).toMillis();
					double weight = new NwmWeight(matches.getMatchesSet(), world.getNumberOfInputModels(), true).sum();
					// System.out.printf((Locale)null, " (%.3f) ", weight);
					nwmWeight += weight;
				}
				double avgFactor = 1.0d / worlds.size();
				System.out.printf((Locale)null, "\n Weight:  %.4f\n", avgFactor * nwmWeight);
				Object times = String.format("(load: %s find: %s match: %s)", toRubinCpuSeconds(avgFactor * loadTimeElapsedMillis),
						toRubinCpuSeconds(avgFactor * searchTimeElapsedMillis), toRubinCpuSeconds(avgFactor * matchTimeElapsedMillis));
				System.out.println(" Time:    "
						+ toRubinCpuSeconds(avgFactor * (loadTimeElapsedMillis + searchTimeElapsedMillis + matchTimeElapsedMillis)) + " " + times);
				}
			}

		System.out.println("¹ Times are normalized to an Intel(R) Core(TM)2 Quad CPU Q8200 @ 2.33GHz");
	}

	private static void warmUp() throws IOException
	{
		for (String testCase : TEST_CASES) {
			NModelWorldLoader.loadChunks(option, "testdata/" + testCase + ".csv", 10);
		}
	}

	private static String toRubinCpuSeconds(double timeElapsedMillis)
	{
		/* Gr3 Warehouse Rubin / local run */
		double TIME_FACTOR = 45.4d / 16.392d; // 2.7696437286481213
		/* NwM Warehouse Rubin / local run */
		// double TIME_FACTOR = 2.9d * 60 / 64.532d; // 2.696336701171512
		/* NwM Hospital Rubin / local run */
		// double TIME_FACTOR = 42.7d / 19.575d // 2.1813537675606645
		/*
		 * https://www.cpubenchmark.net/cpu.php?id=2713
		 * https://www.cpubenchmark.net/cpu.php?id=1040
		 */
		// double TIME_FACTOR = 5648d / 2812d; // 2.008534850640114
		return Math.round(TIME_FACTOR * timeElapsedMillis) / 1000d + " s¹";
	}
}
