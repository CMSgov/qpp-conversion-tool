package gov.cms.qpp.conversion;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


/**
 * Performance test harness.
 */
public class ConverterBenchmark implements FilenameFilter {

	static final File SAMPLES_DIR = new File("src/main/resources/qrda-files/");
	static final String EXTENSION = "qpp.json";
	static final int ITERATIONS = 3;
	
	DescriptiveStatistics stats = new DescriptiveStatistics();
	int iterations = ITERATIONS;
	File path = SAMPLES_DIR;
	
	public boolean accept(File file, String name) {
		return new File(file,name).isFile() && name.endsWith(EXTENSION);
	}
	
	public void doTearDown() {
		// find all the files that were created
		File[] dirFiles = path.listFiles((f,n)->accept(f,n));
		File[] workingFiles = new File(".").listFiles((f,n)->accept(f,n));
		
		// remove all those files 
		Stream.concat(Arrays.stream(dirFiles), Arrays.stream(workingFiles))
		      .filter(f->f.exists())
		      .forEach(f->f.delete());
	}

	
	public boolean doBenchmarks() throws IOException {
		try (FileWriter benchFile = new FileWriter(new File("benchmarks.dat"));) {
			
			// Preliminaries: header and ensure path exists
			System.out.println("Samples path is " + path);
			
			if ( ! path.exists() ) {
				return false;
			}
			
			benchFile.write("file,mean,std,iterations\n");
			
			// for each file - execute benchmark
			for (File file : path.listFiles()) {
				// execute a give number of times
				for (int i=0; i<iterations; i++) {
					long start = System.currentTimeMillis();
					
					 // the target
					ConversionEntry.main(file.getAbsolutePath());
					
					long finish = System.currentTimeMillis();
					stats.addValue( (finish-start) / 1000);
					
					doTearDown();
				}
				// the benchmark
				double mean = stats.getMean();
				double std  = stats.getStandardDeviation();
				
				// report
				benchFile.write(file+","+mean+","+std+","+iterations+"\n");
			}
		}
		return true;
	}
	
	
	public static void main(String ... args) throws IOException {
		ConverterBenchmark bench = new ConverterBenchmark();
		
		// bench if if the files path is present 
		if ( ! bench.doBenchmarks() ) {
			System.out.println("Samples path does not exist.");
		}
	}

}



