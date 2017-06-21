package gov.cms.qpp.conversion;


import org.junit.BeforeClass;
import org.junit.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class ParameterizedBenchmarkTest {
	private static String[] paths;

	@BeforeClass
	public static void loadPaths() throws IOException {
		paths = Files.walk(Paths.get("../qrda-files"))
				.filter(Files::isRegularFile)
				.map(path -> path.toFile().getAbsolutePath())
				.filter(path -> path.endsWith("-latest.xml"))
				.toArray(String[]::new);
	}

	@Test
	public void testNumPaths() {
		assertThat("2 test files are expected", paths.length, is(2));
	}

	@Test
	public void testParameterizedBenchmarkThroughput() throws RunnerException {
		Options opt = getOptions(Mode.Throughput, ParameterizedBenchmark.class);
		Stream<BenchmarkResult> results = executeBenchmark(opt);

		results.forEach(br -> {
			String fileName = br.getParams().getParam("fileName");
			Result result = br.getPrimaryResult();

			assertThat("Throughput should be greater than 2 for file: " + fileName,
					result.getScore(), greaterThan(2.0));
		});
	}

	@Test
	public void testParameterizedBenchmarkAverageTime() throws RunnerException {
		Options opt = getOptions(Mode.AverageTime, ParameterizedBenchmark.class);
		Stream<BenchmarkResult> results = executeBenchmark(opt);

		results.forEach(br -> {
			String fileName = br.getParams().getParam("fileName");
			Result result = br.getPrimaryResult();

			assertThat("Average time should be less than .5 seconds" + fileName,
					result.getScore(), lessThan(0.5));
		});
	}

	private Options getOptions(Mode mode, Class benchmarkClass) {
		return new OptionsBuilder()
				.mode(mode)
				.include(".*" + benchmarkClass.getSimpleName() + ".*")
				.param("fileName", paths)
				.forks(1)
				.build();
	}

	private Stream<BenchmarkResult> executeBenchmark(Options opt) throws RunnerException {
		List<RunResult> results = new ArrayList<>(new Runner(opt).run());
		assertThat("Should return a benchmark result for each path", results, hasSize(paths.length));
		return results.stream().map(RunResult::getAggregatedResult);
	}
}
