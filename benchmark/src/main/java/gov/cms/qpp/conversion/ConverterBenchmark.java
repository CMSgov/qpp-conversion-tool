package gov.cms.qpp.conversion;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


/**
 * Performance test harness.
 */
public class ConverterBenchmark implements FilenameFilter {

	static final File   SAMPLES_DIR     = new File("src/main/resources/qrda-files/");
	static final String JSON_EXTENSION  = "qpp.json";
	static final String INPUT_EXTENSION = ".xml";
	static final String FORCE      = "--force";
	static final String HELP       = "--help";
	static final int    ITERATIONS = 3;
	static final int    PRECISION = 4;
	
	int iterations = ITERATIONS;
	LinkedList<File> paths = new LinkedList<>();
	boolean force = false;
	boolean firstFile = true;
	
	
	@Override
	public boolean accept(File file, String name) {
		return new File(file,name).isFile() && name.endsWith(JSON_EXTENSION);
	}
	
	
	protected void doTearDown(File path) {
		// find all the files that were created
		File[] dirFiles = path.listFiles((f,n)->accept(f,n));
		File[] workingFiles = new File(".").listFiles((f,n)->accept(f,n));
		
		// remove all those files 
		Stream.concat(Arrays.stream(dirFiles), Arrays.stream(workingFiles))
		      .filter(f->f.exists())
		      .forEach(f->f.delete());
	}

	
	public void doBenchmarks() throws IOException {
		try (FileWriter benchFile = new FileWriter(new File("benchmarks.dat"));) {
			benchFile.write("file,mean,std,iterations/files\n");
			
			// for each file in each path - execute benchmark
			for (File path : paths) {
				DescriptiveStatistics pathStats = new DescriptiveStatistics();
				
				int fileCount = 0;
				for (File file : path.listFiles(
						(f,n)->new File(f,n).isFile() && n.toLowerCase().endsWith(INPUT_EXTENSION))) {
					DescriptiveStatistics fileStats = new DescriptiveStatistics();
					fileCount++;

					// execute a give number of times
					for (int i=0; i<iterations; i++) {
						long start = System.currentTimeMillis();
						
						 // the target
						ConversionEntry.main(file.getAbsolutePath());
						
						long finish = System.currentTimeMillis();
						if (firstFile) {
							firstFile = false;
							i--;
							continue;
						}
						fileStats.addValue( (finish-start) / 1000.0);
					}
					reportStats(file, benchFile, fileStats, iterations);
					pathStats.addValue(fileStats.getMean());
				}
				reportStats(path, benchFile, pathStats, fileCount);
				
				doTearDown(path);
			}
		}
	}
	
	
	protected void reportStats(File what, Writer reporter, DescriptiveStatistics stats, int count) throws IOException {
		// report the file benchmark
		BigDecimal mean = new BigDecimal(stats.getMean()).setScale(PRECISION, RoundingMode.HALF_UP);
		BigDecimal stdv = new BigDecimal(stats.getStandardDeviation()).setScale(PRECISION, RoundingMode.HALF_UP);
		reporter.write(what+","+mean+","+stdv+","+count+"\n");
	}
	
	
	protected boolean parseArgs(String[] args) {
		boolean validArgs = true;
		
		for (String arg : args) {
			if (FORCE.equals(arg)) {
				force = true;
				continue;
			}
			if (HELP.equals(arg)) {
				printHelp();
				force = false;
				return false;
			}
			
			try {
				int number = Integer.parseInt(arg);
				iterations = number;
			} catch (NumberFormatException e) {
				File path = new File(arg);
				if ( ! path.exists() ) {
					validArgs = false;
					System.out.println("Samples path does not exist: " + arg);
				} else {
					paths.add(new File(arg));
				}
			}
		}
		if (paths.isEmpty()) {
			paths.add(SAMPLES_DIR);
		}
		return validArgs;
	}
	
	
	protected void printHelp() {
		System.out.println("[options] [iterations] [path1, path2, ...] ");
		System.out.println("iterations - (default "+ITERATIONS+") is an option integer number of times to test each file for a mean/average runtime and standard divation.");
		System.out.println("             If the stdev is high then the parse times are wildly variable an unpredictable.");
		System.out.println("paths - are an optional list of directories to find XML QPP files to parse. (default "+SAMPLES_DIR+")");
		System.out.println(HELP  + " display this message");
		System.out.println(FORCE + " will run with partial valid paths, with out this it will run only if all paths are valid");
	}

	
	public static void main(String ... args) throws IOException {
		ConverterBenchmark bench = new ConverterBenchmark();
		
		// bench if if the files path is present 
		if ( bench.parseArgs(args) || bench.force) {
			// Preliminaries: header and ensure path exists
			System.out.println("Samples paths are " + bench.paths);
			bench.doBenchmarks();
		}
	}

}



