package gov.cms.qpp.conversion;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class ConverterTestWithAbridged {

	@Test
	public void testWithAbridgedXml() {
		long start = System.currentTimeMillis();

		Converter.main(new String[] { "src/test/resources/valid-QRDA-III-abridged.xml" });

		long finish = System.currentTimeMillis();

		File aJson = new File("valid-QRDA-III-abridged.qpp.json");

		assertTrue(aJson.exists());

		aJson.deleteOnExit();

		System.out.println("Time to run transform " + (finish - start));
	}
}
