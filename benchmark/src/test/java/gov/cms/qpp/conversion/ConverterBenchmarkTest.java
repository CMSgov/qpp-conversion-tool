package gov.cms.qpp.conversion;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Before;
import org.junit.Test;

public class ConverterBenchmarkTest {

	ConverterBenchmark bench = new ConverterBenchmark();
	
	@Before
	public void before() {
		bench = new ConverterBenchmark();
	}
	
	@Test
	public void testAccept_true() throws Exception {
		File testFile = new File("test.qpp.json");
		try (FileWriter writer = new FileWriter(testFile)) {
			writer.write("test data");
			assertTrue("Expect to find a qpp.json file that exists",
					bench.accept(new File("."), testFile.getName()) );
			
		} finally {
			delete(testFile);
		}
	}
	@Test
	public void testAccept_false() throws Exception {
		assertFalse("Do NOT expect to find a qpp.json file that isn't present",
				bench.accept(new File("."), "any.qpp.json") );
		
		File testFile = new File("test.file");
		try (FileWriter writer = new FileWriter(testFile)) {
			writer.write("test data");
			assertFalse("Do NOT expect to accept a non qpp.json file that exists",
					bench.accept(new File("."), testFile.getName()) );

		} finally {
			delete(testFile);
		}
	}
	
	
	@Test
	public void testDoTearDown_delete() throws Exception {
		File testFile = new File("test.qpp.json");
		try (FileWriter writer = new FileWriter(testFile)) {
			writer.write("test data");
		}
		assertTrue("The test file is not present to test doTearDown", testFile.exists());
		bench.doTearDown(new File("."));
		assertFalse("Expect qpp.json files to be deleted.", testFile.exists() );
	}
	
	@Test
	public void testDoTearDown_noDelete() throws Exception {
		File testFile = new File("test.qpp.other");
		try {
			try (FileWriter writer = new FileWriter(testFile)) {
				writer.write("test data");
			}
			assertTrue("The test file is not present to test doTearDown", testFile.exists());
			bench.doTearDown(new File("."));
			assertTrue("Expect non qpp.json files to remain.", testFile.exists() );

		} finally {
			delete(testFile);
		}
	}
	
	@Test
	public void testReportStats() throws Exception {
		File testFile = new File("test.qpp.other");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(baos);
		
		DescriptiveStatistics stats = new DescriptiveStatistics();
		stats.addValue(1);
		
		bench.reportStats(testFile, writer, stats, 9);

		writer.close();
		baos.flush();
		baos.close();
		
		String outString = new String(baos.toByteArray());
		
		assertEquals("test.qpp.other,1.0000,0.0000,9\n", outString);
	}
	
	@Test
	public void testParseArgs_force1() throws Exception {
		boolean valid = bench.parseArgs(new String[] {"--force"});
		assertTrue("Expect valid args", valid);
		assertTrue("force should be detected and set benchmark instance force state", bench.force );
	}
	@Test
	public void testParseArgs_force2() throws Exception {
		boolean valid = bench.parseArgs(new String[] {"3", "--force"});
		assertTrue("Expect valid args", valid);
		assertTrue("force should be detected and set benchmark instance force state", bench.force );
	}
	@Test
	public void testParseArgs_pathsNotExistsForce() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(baos);
		PrintStream out = System.out;
		try {
			System.setOut(print);
		
			boolean valid = bench.parseArgs(new String[] {"--force", "asdf"});
			assertFalse("Expect not valid args return when bad paths and force, force state will take care of that", valid);
			assertTrue("force should be detected and set benchmark instance force state", bench.force );
			
			String outString = new String(baos.toByteArray());

			assertEquals("Samples path does not exist: asdf\n", outString);
		} finally {
			System.setOut(out);
		}
	}

	@Test
	public void testParseArgs_help1() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(baos);
		PrintStream out = System.out;
		try {
			System.setOut(print);
			boolean valid = bench.parseArgs(new String[] {"--help"});
			assertFalse("Expect non valid args returned to not run on help", valid);

			String outString = new String(baos.toByteArray());

			assertTrue("help should be detected and print the help",
					outString.startsWith("[options]"));
			assertTrue("help should be detected and print the help",
					outString.contains("--force"));
			assertTrue("help should be detected and print the help",
					outString.contains("--help"));
			assertTrue("help should be detected and print the help",
					outString.contains("iterations - (default 3)"));
		} finally {
			System.setOut(out);
		}
	}
	@Test
	public void testParseArgs_help2() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(baos);
		PrintStream out = System.out;
		try {
			System.setOut(print);
			boolean valid = bench.parseArgs(new String[] {"3", "--help"});
			assertFalse("Expect non valid args returned to not run on help", valid);

			String outString = new String(baos.toByteArray());

			assertTrue("help should be detected and print the help",
					outString.startsWith("[options]"));
			assertTrue("help should be detected and print the help",
					outString.contains("--force"));
			assertTrue("help should be detected and print the help",
					outString.contains("--help"));
			assertTrue("help should be detected and print the help",
					outString.contains("iterations - (default 3)"));
		} finally {
			System.setOut(out);
		}
	}
	
	@Test
	public void testParseArgs_iterationsDefault() throws Exception {
		boolean valid = bench.parseArgs(new String[] {});
		assertTrue("Expect valid args", valid);
		assertEquals("Expect default iterations when none given", bench.ITERATIONS, bench.iterations );
	}
	@Test
	public void testParseArgs_iterations1() throws Exception {
		boolean valid = bench.parseArgs(new String[] {"1"});
		assertTrue("Expect valid args", valid);
		assertEquals("Expected 1 iteration when given", 1, bench.iterations );
	}
	@Test
	public void testParseArgs_iterations2() throws Exception {
		boolean valid = bench.parseArgs(new String[] {"--force", "2"});
		assertTrue("Expect valid args", valid);
		assertEquals("Expected 2 iteration when given", 2, bench.iterations );
	}
	
	
	@Test
	public void testParseArgs_pathExists() throws Exception {
		boolean valid = bench.parseArgs(new String[] {"."});
		assertTrue("Expect valid args", valid);
		assertEquals("Expected 1 path that exists ", 1, bench.paths.size() );
	}
	@Test
	public void testParseArgs_pathsExist() throws Exception {
		boolean valid = bench.parseArgs(new String[] {".", ".."});
		assertTrue("Expect valid args", valid);
		assertEquals("Expected 2 paths that exist ", 2, bench.paths.size() );
	}
	
	@Test
	public void testParseArgs_pathsNotExists() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(baos);
		PrintStream out = System.out;
		try {
			System.setOut(print);
		
			boolean valid = bench.parseArgs(new String[] {"foo", "asdf"});
			assertFalse("Expect not valid args when bad paths", valid);
			
			String outString = new String(baos.toByteArray());

			assertEquals("Samples path does not exist: foo\nSamples path does not exist: asdf\n", outString);
			assertEquals("Expected 1 paths that exist default", 1, bench.paths.size() );
			assertEquals("Expected default path when all bad or none given", bench.SAMPLES_DIR, bench.paths.get(0) );
		} finally {
			System.setOut(out);
		}
	}
	
	@Test
	public void testDoBenchmarks_single() throws Exception {
		bench = new ConverterBenchmark() {
			@Override
			protected void doProfileAction(File file) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		bench.paths.clear();
		bench.paths.add( new File("src/test/") );
		
		File testFile = new File("src/test/test.qpp.xml");
		try {
			createFile(testFile);
			
			bench.doBenchmarks();
			
			try (BufferedReader dat = new BufferedReader(
					new FileReader( bench.BENCHMARK_DATA ) ) ) {
			
				String actual = dat.readLine();
				assertEquals("file,mean,std,iterations/files",actual);
				
				actual = dat.readLine();
				Pattern expected = Pattern.compile("src/test/"+testFile.getName()+",0.5\\d\\d\\d,0.0\\d\\d\\d,3");
				assertTrue( expected.matcher(actual).matches() );
				
				actual = dat.readLine();
				expected = Pattern.compile("src/test,0.5\\d\\d\\d,0.0\\d\\d\\d,1");
				assertTrue( expected.matcher(actual).matches() );
			}
		} finally {
			delete(testFile);
			cleanup();
		}
		
	}
	
	@Test
	public void testDoBenchmarks_couple() throws Exception {
		bench = new ConverterBenchmark() {
			int sleeptime = 100; // first test file time
			File firstTestFile = null;
			@Override
			protected void doProfileAction(File file) {
				if (firstTestFile == null) {
					firstTestFile = file;
				}
				if (firstTestFile != file) {
					sleeptime = 300; // second test file time
				}
				
				try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		bench.paths.clear();
		bench.paths.add( new File("src/test/") );
		
		File testFile1 = new File("src/test/test1.qpp.xml");
		File testFile2 = new File("src/test/test2.qpp.xml");
		File testFile3 = new File("src/test/test2.qpp.not"); // this file will not be processed
		try {
			createFile(testFile1);
			createFile(testFile2);
			createFile(testFile3);
			
			bench.doBenchmarks();
			
			try (BufferedReader dat = new BufferedReader(
					new FileReader( bench.BENCHMARK_DATA ) ) ) {
			
				String actual = dat.readLine();
				assertEquals("file,mean,std,iterations/files",actual);
				
				actual = dat.readLine();
				Pattern expected = Pattern.compile("src/test/"+testFile1.getName()+",0.1\\d\\d\\d,0.0\\d\\d\\d,3");
				assertTrue( expected.matcher(actual).matches() );
				
				actual = dat.readLine();
				expected = Pattern.compile("src/test/"+testFile2.getName()+",0.3\\d\\d\\d,0.0\\d\\d\\d,3");
				assertTrue( expected.matcher(actual).matches() );
				
				actual = dat.readLine();
				expected = Pattern.compile("src/test,0.2\\d\\d\\d,0.1\\d\\d\\d,2");
				assertTrue( expected.matcher(actual).matches() );
			}
		} finally {
			delete(testFile1);
			delete(testFile2);
			delete(testFile3);
			cleanup();
		}
		
	}
	
	private void cleanup() {
		delete( bench.BENCHMARK_DATA );
		delete( new File("benchmarks.err") );
		delete( new File("benchmarks.log") );
	}
	private void delete(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
	private void createFile(File testFile) throws IOException {
		try (FileWriter writer = new FileWriter(testFile)) {
			writer.write("test data");
		}
	}
}
