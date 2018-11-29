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
			Option option = testCase.startsWith("random") ? Option.CLASSES_ONLY : Option.ALL;
			System.out.println("Using " + testCase + " dataset (" + option + ")...");
			Instant startedAt = Instant.now();
			NModelWorld world = NModelWorldLoader.load(option, "testdata/" + testCase + ".csv");
			Instant loadedAt = Instant.now();
			Similarities allSimilarities = world.findSimilarities(2.5);
			Instant foundSimilaritiesAt = Instant.now();
			AbstractMatches matches = new GreedyDistanceMatches(allSimilarities);
			Instant finishedAt = Instant.now();
			NwmWeight nwmWeight = new NwmWeight(matches.getMatchesSet(), world.getNumberOfInputModels());
			System.out.println(" Weight:  " + nwmWeight.sum());
			System.out.println(" Time:    " + toSeconds(startedAt, finishedAt));
			System.out.println("  load:   " + toSeconds(startedAt, loadedAt));
			System.out.println("  search: " + toSeconds(loadedAt, foundSimilaritiesAt));
			System.out.println("  match:  " + toSeconds(foundSimilaritiesAt, finishedAt));
			System.out.println();
		}
	}

	private static double toSeconds(Instant startedAt, Instant finishedAt)
	{
		long timeElapsedMillis = Duration.between(startedAt, finishedAt).toMillis();
		return timeElapsedMillis / 1000d;
	}
}
