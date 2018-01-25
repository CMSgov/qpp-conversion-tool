package gov.cms.qpp.conversion;

import com.google.common.collect.Lists;

import java.nio.file.Paths;
import java.util.List;

/**
 * A suite of conversions where the time is averaged and then averaged again to give a relatively stable time in milliseconds
 * that a conversion takes.
 */
public class SpeedTest {
	private static final int SETS_PER_SUITE = 10;
	private static final int TESTS_PER_SET = 20;
	private static final PathSource FILE_TO_CONVERT = new PathSource(Paths.get("./qrda-files/valid-QRDA-III-latest.xml"));

	/**
	 * Run the speed suite and out the results.
	 *
	 * @param args Arguments passed to the program.
	 * @throws InterruptedException When sleeping for one second is interrupted.
	 */
	public static void main(String... args) throws InterruptedException {
		System.out.println("Average of averages=" + testSuite());
	}

	/**
	 * Run multiple sets of conversions.
	 *
	 * @return The average of the averages.
	 * @throws InterruptedException When sleeping for one second is interrupted.
	 */
	private static Double testSuite() throws InterruptedException {
		List<Double> testSuite = Lists.newArrayList();

		for (int lcv = 0; lcv < SETS_PER_SUITE; lcv++) {
			testSuite.add(testSet());
			Thread.sleep(1000);
		}

		return testSuite.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
	}

	/**
	 * Run a set of conversions. Throw out the first conversion because sometimes it is extra slow
	 *
	 * @return The average of all of the conversions.
	 */
	private static Double testSet() {
		List<Long> testSet = Lists.newArrayList();

		for (int lcv = 0; lcv < TESTS_PER_SET + 1; lcv++) {
			testSet.add(individualTest());
		}

		testSet.remove(0);

		return testSet.stream().mapToLong(Long::longValue).average().getAsDouble();
	}

	/**
	 * Run a individual conversion.
	 *
	 * @return The time it took in milliseconds.
	 */
	private static Long individualTest() {
		long startTime = System.currentTimeMillis();
		new Converter(FILE_TO_CONVERT).transform();
		long stopTime = System.currentTimeMillis();

		return stopTime - startTime;
	}
}
