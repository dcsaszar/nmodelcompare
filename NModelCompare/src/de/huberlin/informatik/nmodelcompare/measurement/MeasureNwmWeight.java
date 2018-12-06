package de.huberlin.informatik.nmodelcompare.measurement;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

public class MeasureNwmWeight
{
	final static List<String> RUBIN_TEST_CASES = Arrays.asList("hospitals", "warehouses", "random", "randomLoose", "randomTight");

	public static void main(String... args) throws IOException
	{
		for (String testCase : RUBIN_TEST_CASES) {
			for (Option option : testCase.startsWith("random") ? Arrays.asList(Option.CLASSES_ONLY) : Arrays.asList(Option.ALL, Option.CLASSES_ONLY)) {
				System.out.println("Using " + testCase + " dataset (" + option + ")...");
				Instant startedAt = Instant.now();
				NModelWorld world = NModelWorldLoader.load(option, "testdata/" + testCase + ".csv");
				Instant loadedAt = Instant.now();
				Similarities allSimilarities = world.findSimilarities(2.5);
				Instant foundSimilaritiesAt = Instant.now();
				AbstractMatches matches = new WeightOptimizedMatches(allSimilarities);
				Instant finishedAt = Instant.now();
				NwmWeight nwmWeight = new NwmWeight(matches.getMatchesSet(), world.getNumberOfInputModels());
				System.out.println(" Weight:  " + nwmWeight.sum());
				System.out.println(" Time:    " + toRubinCpuSeconds(startedAt, finishedAt));
				System.out.println("  load:   " + toRubinCpuSeconds(startedAt, loadedAt));
				System.out.println("  search: " + toRubinCpuSeconds(loadedAt, foundSimilaritiesAt));
				System.out.println("  match:  " + toRubinCpuSeconds(foundSimilaritiesAt, finishedAt));
				System.out.println();
			}
		}

		System.out.println("¹ Times are normalized to an Intel(R) Core(TM)2 Quad CPU Q8200 @ 2.33GHz");
	}

	private static String toRubinCpuSeconds(Instant startedAt, Instant finishedAt)
	{
		/*
		 * https://www.cpubenchmark.net/cpu.php?id=2713
		 * https://www.cpubenchmark.net/cpu.php?id=1040
		 */
		double TIME_FACTOR = 5648d / 2812d;

		long timeElapsedMillis = Duration.between(startedAt, finishedAt).toMillis();
		return Math.round(TIME_FACTOR * timeElapsedMillis) / 1000d + "s¹";
	}
}
