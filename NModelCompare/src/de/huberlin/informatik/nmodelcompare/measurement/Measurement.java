package de.huberlin.informatik.nmodelcompare.measurement;

import java.util.*;

public class Measurement
{
	public Measurement(int runId, String testCase, double radius, int chunksCount, int chunkNumber, int lookahead)
	{
		this.runId = runId;
		this.testCase = testCase;
		this.radius = radius;
		this.chunksCount = chunksCount;
		this.chunkNumber = chunkNumber;
		this.lookahead = lookahead;
	}

	@Override
	public String toString()
	{
		return String.format(Locale.ENGLISH, "#%d\t%.3f\t%s\t%2d\t%2d\t%.4f\t%.5f\t%.5f\t%.5f\t%.5f\t%2d", runId, radius, testCase, chunkNumber,
				chunksCount,
				resultNwmWeight, resultIndexTimeElapsedRubinSec + resultSearchTimeElapsedRubinSec + resultMatchTimeElapsedRubinSec,
				resultIndexTimeElapsedRubinSec,
				resultSearchTimeElapsedRubinSec,
				resultMatchTimeElapsedRubinSec, lookahead);
	}

	public static Measurement average(List<Measurement> measurementsForAvg)
	{
		Measurement last = measurementsForAvg.get(measurementsForAvg.size() - 1);
		Measurement avgMeasurement = new Measurement(last.runId, last.testCase, last.radius, last.chunksCount, last.chunkNumber, last.lookahead);
		avgMeasurement.resultNwmWeight = measurementsForAvg.stream().mapToDouble(r -> r.resultNwmWeight).average().getAsDouble();

		avgMeasurement.resultIndexTimeElapsedRubinSec = measurementsForAvg.stream().mapToDouble(r -> r.resultIndexTimeElapsedRubinSec).average()
				.getAsDouble();
		avgMeasurement.resultSearchTimeElapsedRubinSec = measurementsForAvg.stream().mapToDouble(r -> r.resultSearchTimeElapsedRubinSec).average().getAsDouble();
		avgMeasurement.resultMatchTimeElapsedRubinSec = measurementsForAvg.stream().mapToDouble(r -> r.resultMatchTimeElapsedRubinSec).average().getAsDouble();
		return avgMeasurement;
	}

	public static Measurement parseString(String string)
	{
		Scanner s = new Scanner(string);
		s.useLocale(Locale.ENGLISH);
		s.skip("[^#]*#");

		int runId = s.nextInt();
		double radius = s.nextDouble();
		String testCase = s.next();
		int chunkNumber = s.nextInt();
		int chunksCount = s.nextInt();

		double resultNwmWeight = s.nextDouble();
		s.nextDouble(); // overall time
		double resultIndexTimeElapsedRubinSec = s.nextDouble();
		double resultSearchTimeElapsedRubinSec = s.nextDouble();
		double resultMatchTimeElapsedRubinSec = s.nextDouble();

		int lookahead = s.nextInt();
		s.close();

		Measurement measurement = new Measurement(runId, testCase, radius, chunksCount, chunkNumber, lookahead);
		measurement.resultNwmWeight = resultNwmWeight;
		measurement.resultIndexTimeElapsedRubinSec = resultIndexTimeElapsedRubinSec;
		measurement.resultSearchTimeElapsedRubinSec = resultSearchTimeElapsedRubinSec;
		measurement.resultMatchTimeElapsedRubinSec = resultMatchTimeElapsedRubinSec;
		return measurement;
	}

	public int runId;
	public String testCase;
	public double radius;
	public int chunksCount;
	public int chunkNumber;
	public int lookahead;
	public double resultNwmWeight;
	public double resultIndexTimeElapsedRubinSec;
	public double resultSearchTimeElapsedRubinSec;
	public double resultMatchTimeElapsedRubinSec;
}