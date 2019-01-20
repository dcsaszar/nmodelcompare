package de.huberlin.informatik.nmodelcompare.measurement;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.huberlin.informatik.nmodelcompare.*;
import de.huberlin.informatik.nmodelcompare.NModelWorldLoader.Option;

public class MeasureNwmWeight
{
	final static List<String> TEST_CASES = Arrays.asList("hospitals", "warehouses", "random", "randomLoose", "randomTight");
	final static List<Double> RADII = Arrays.asList(0.0, 2.5, 2.75, 3.0, 3.125, 3.25, 3.5, 3.75, 4.0, 4.25, 4.5, 4.75, 5.0, 5.5, 6.5, 7.5);
	final static Option option = Option.CLASSES_ONLY;
	final static List<Measurement> measurements = new ArrayList<>();

	public static void main(String... args) throws IOException
	{
		warmUp();
		setupTestCases();

		boolean isMicroBenchmarkRun = args.length == 2;
		int warmupLoopCount = isMicroBenchmarkRun ? Integer.parseInt(args[1]) : 0;

		IntStream runIds;

		if (isMicroBenchmarkRun) {
			Integer requestedRunId = Integer.parseInt(args[0]);
			runIds = IntStream.rangeClosed(requestedRunId, requestedRunId);
		}
		else {
			runIds = IntStream.range(0, measurements.size());
		}

		runIds.forEachOrdered(runId -> {
			for (int remainingLoopCount = warmupLoopCount; remainingLoopCount >= 0; remainingLoopCount--) {
				Measurement measurement = measurements.get(runId);
				int chunksCount = measurement.chunksCount;
				int chunkSize = chunksCount == 1 ? Integer.MAX_VALUE : 10;
				int chunkNumber = measurement.chunkNumber;

				List<NModelWorld> worlds;

				try {
					worlds = NModelWorldLoader.loadChunks(option, "testdata/" + measurement.testCase + ".csv", chunkSize);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}

				NModelWorld world = worlds.get(chunkNumber - 1);

				Instant startedAt = Instant.now();
				world.buildIndex();
				Instant indexBuiltAt = Instant.now();
				Similarities allSimilarities = world.findSimilarities(measurement.radius);
				Instant foundSimilaritiesAt = Instant.now();
				AbstractMatches matches = new WeightOptimizedMatches(allSimilarities);
				Instant finishedAt = Instant.now();

				measurement.resultNwmWeight = new NwmWeight(matches.getMatchesSet(), world.getNumberOfInputModels(), true).sum();

				measurement.resultIndexTimeElapsedRubinSec = toRubinCpuSeconds(Duration.between(startedAt, indexBuiltAt));
				measurement.resultSearchTimeElapsedRubinSec = toRubinCpuSeconds(Duration.between(indexBuiltAt, foundSimilaritiesAt));
				measurement.resultMatchTimeElapsedRubinSec = toRubinCpuSeconds(Duration.between(foundSimilaritiesAt, finishedAt));

				if (remainingLoopCount == 0) {
					System.out.println(measurement.toString());

					if (isMicroBenchmarkRun || measurement.chunkNumber == measurement.chunksCount) {
						IntStream avgRunIds = IntStream
								.rangeClosed(isMicroBenchmarkRun ? measurement.runId : measurement.runId - measurement.chunksCount + 1, measurement.runId);
						List<Measurement> measurementsForAvg = avgRunIds.mapToObj(id -> measurements.get(id)).collect(Collectors.toList());
						Measurement avgMeasurement = Measurement.average(measurementsForAvg);
						System.out.printf(Locale.ENGLISH,
								"Weight: %.4f Time %.4f s¹ | ¹ Times are normalized to an Intel(R) Core(TM)2 Quad CPU Q8200 @ 2.33GHz\n\n",
								avgMeasurement.resultNwmWeight,
								avgMeasurement.resultIndexTimeElapsedRubinSec + avgMeasurement.resultSearchTimeElapsedRubinSec
										+ avgMeasurement.resultMatchTimeElapsedRubinSec);
					}
				}
			}
		});
	}

	private static void warmUp() throws IOException
	{
		Instant startedAt = Instant.now();
		OptionalDouble weight = IntStream.range(0, 5).mapToDouble(i -> {
			NModelWorld world;
			try {
				world = NModelWorldLoader.loadChunks(option, "testdata/random_sample270.csv", 5).get(i);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			AbstractMatches matches = new WeightOptimizedMatches(world.findSimilarities(i * 1.5d));
			return (new NwmWeight(matches.getMatchesSet(), world.getNumberOfInputModels(), true).sum());
		}).average();
		Instant finishedAt = Instant.now();
		long time = Duration.between(startedAt, finishedAt).toMillis();
		System.out.println("Warm up: " + time + " ms, " + (weight.getAsDouble() == 0d ? "" : "."));
	}

	private static void setupTestCases()
	{
		int runId = 0;
		for (double radius : RADII) {
			for (String testCase : TEST_CASES) {
				boolean useAverage = testCase.startsWith("random");
				int chunksCount = useAverage ? 10 : 1;
				for (int chunkNumber = 1; chunkNumber <= chunksCount; chunkNumber++) {
					measurements.add(new Measurement(runId++, testCase, radius, chunksCount, chunkNumber));
				}
			}
		}
	}

	private static double toRubinCpuSeconds(Duration duration)
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
		return TIME_FACTOR * duration.toMillis() / 1000d;
	}
}
