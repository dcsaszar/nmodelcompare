package de.huberlin.informatik.nmodelcompare.measurement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class MeasureNwmWeightMicroBenchmark
{
	public static void main(String... args) throws IOException, InterruptedException
	{
		int runId = 0;
		int WARMUP_LOOPS = 0;

		List<Measurement> measurements = new ArrayList<>();
		List<Measurement> measurementsAvg = new ArrayList<>();

		while (true) {
			Process process = Runtime.getRuntime()
					.exec("java -cp bin" + File.pathSeparator + "lib/* de.huberlin.informatik.nmodelcompare.measurement.MeasureNwmWeight " + runId + " "
							+ WARMUP_LOOPS);
			process.waitFor();

			if (process.exitValue() != 0) {
				String error = IOUtils.toString(process.getErrorStream());
				System.out.println("Exiting.\n" + error);
				break;
			}

			String output = IOUtils.toString(process.getInputStream());
			Measurement measurement = Measurement.parseString(output);
			measurements.add(measurement);
			measurementsAvg.add(measurement);

			if (measurement.chunksCount == measurementsAvg.size()) {
				System.out.println(Measurement.average(measurementsAvg));
				measurementsAvg.clear();
			}

			runId++;
		}
	}
}
