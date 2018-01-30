package gov.cms.qpp.conversion;

import com.google.common.collect.Lists;

import java.nio.file.Paths;
import java.util.List;

/**
 * A suite of conversions where the time is averaged and then averaged again to give a relatively stable time in milliseconds
 * that a conversion takes.
 */
public class SpeedTest {
	private static final int TESTS_FOR_WARMUP = 1000;
	private static final int SETS_PER_SUITE = 10;
	private static final int TESTS_PER_SET = 20;
	private static final PathSource FILE_TO_CONVERT = new PathSource(Paths.get("./qrda-files/valid-QRDA-III-latest.xml"));

	/**
	 * Run the speed suite and out the results.
	 *
	 * @param args Arguments passed to the program.
	 */
	public static void main(String... args) {
		System.out.println("Warming-up");
		multipleTests(TESTS_FOR_WARMUP);
		System.out.println("Finished warming-up");
		System.out.println("Average of averages=" + testSuite());
	}

	/**
	 * Run multiple sets of conversions.
	 *
	 * @return The average of the averages.
	 */
	private static Double testSuite() {
		List<Double> testSuite = Lists.newArrayList();

		for (int lcv = 0; lcv < SETS_PER_SUITE; lcv++) {
			testSuite.add(testSet());
		}

		return testSuite.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
	}

	/**
	 * Run a set of conversions. Throw out the first conversion because sometimes it is extra slow
	 *
	 * @return The average of all of the conversions.
	 */
	private static Double testSet() {
		List<Long> testSet = multipleTests(TESTS_PER_SET);

		return testSet.stream().mapToLong(Long::longValue).average().getAsDouble();
	}

	private static List<Long> multipleTests(int numberOfTests) {
		List<Long> testSet = Lists.newArrayList();

		for (int lcv = 0; lcv < numberOfTests; lcv++) {
			testSet.add(individualTest());
		}

		return testSet;
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
