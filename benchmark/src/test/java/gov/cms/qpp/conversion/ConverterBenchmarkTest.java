package gov.cms.qpp.conversion;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;

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

}
