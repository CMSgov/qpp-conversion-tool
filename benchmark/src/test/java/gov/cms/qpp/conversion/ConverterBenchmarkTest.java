package gov.cms.qpp.conversion;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

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
			if (testFile.exists()) {
				testFile.delete();
			}
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
			if (testFile.exists()) {
				testFile.delete();
			}
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
			if (testFile.exists()) {
				testFile.delete();
			}
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
		bench.parseArgs(new String[] {"--force"});
		assertTrue("force should be detected and set benchmark instance force state", bench.force );
	}
	@Test
	public void testParseArgs_force2() throws Exception {
		bench.parseArgs(new String[] {"3", "--force"});
		assertTrue("force should be detected and set benchmark instance force state", bench.force );
	}
	@Test
	public void testParseArgs_help1() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream print = new PrintStream(baos);
		PrintStream out = System.out;
		try {
			System.setOut(print);
			bench.parseArgs(new String[] {"--help"});

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
			bench.parseArgs(new String[] {"3", "--help"});

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
		bench.parseArgs(new String[] {});
		assertEquals("Expect default iterations when none given", bench.ITERATIONS, bench.iterations );
	}
	@Test
	public void testParseArgs_iterations1() throws Exception {
		bench.parseArgs(new String[] {"1"});
		assertEquals("Expected 1 iteration when given", 1, bench.iterations );
	}
	@Test
	public void testParseArgs_iterations2() throws Exception {
		bench.parseArgs(new String[] {"--force", "2"});
		assertEquals("Expected 2 iteration when given", 2, bench.iterations );
	}
}
