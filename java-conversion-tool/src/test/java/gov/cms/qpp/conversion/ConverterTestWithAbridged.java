package gov.cms.qpp.conversion;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class ConverterTestWithAbridged {

	@Test
	public void testWithAbridgedXml() throws IOException {
		long start = System.currentTimeMillis();

		Converter.main(new String[] { "src/test/resources/valid-QRDA-III-abridged.xml" });

		long finish = System.currentTimeMillis();

		Path aJson = Paths.get("valid-QRDA-III-abridged.qpp.json");

		assertTrue(Files.exists(aJson));

		Files.delete(aJson);

		System.out.println("Time to run transform " + (finish - start));
	}
}
