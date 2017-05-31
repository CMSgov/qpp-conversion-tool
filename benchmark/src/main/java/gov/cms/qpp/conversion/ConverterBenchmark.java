package gov.cms.qpp.conversion;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Performance test harness.
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(3)
public class ConverterBenchmark {

	static File SAMPLES_DIR = new File("src/main/resources/qrda-files/");
	static String EXTENSION = "qpp.json";
	
	/**
	 * State management for tests.
	 */
	@State(Scope.Thread)
	public static class Cleaner implements FilenameFilter {
		public boolean accept(File file, String name) {
			return file.isFile() && name.endsWith(EXTENSION);
		}
		
		@TearDown(Level.Trial)
		public void doTearDown() {
			
			// find all the files that were created
			File[] dirFiles = SAMPLES_DIR.listFiles((f,n)->accept(f,n));
			File[] workingFiles = new File(".").listFiles((f,n)->accept(f,n));
			
			// remove all those files 
			Stream.concat(Arrays.stream(dirFiles), Arrays.stream(workingFiles))
			      .filter(f->f.exists())
			      .forEach(f->f.delete());
		}
	}

	/**
	 * Benchmark qrda file conversion.
	 *
	 * @param cleaner State management for conversion runs, ensures that output files are deleted.
	 */
	@Benchmark
	@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
	@OutputTimeUnit(TimeUnit.SECONDS)
	public void benchmarkMain(Cleaner cleaner) {
		// bench if if the files path is present 
		if ( SAMPLES_DIR.exists() ) {
			ConversionEntry.main(SAMPLES_DIR.getAbsolutePath()+"*");
		} else {
			System.err.println("Samples path does not exist. " + SAMPLES_DIR);
		}
	}

}
