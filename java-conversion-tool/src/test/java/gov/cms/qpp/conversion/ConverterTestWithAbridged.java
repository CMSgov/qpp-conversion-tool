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

		Converter.main(new String[] { Converter.SKIP_VALIDATION,
				"src/test/resources/valid-QRDA-III-abridged.xml" });

		long finish = System.currentTimeMillis();

		Path aJson = Paths.get("valid-QRDA-III-abridged.qpp.json");

		assertTrue(Files.exists(aJson));

		Files.delete(aJson);

		System.out.println("Time to run transform " + (finish - start));
	}

	@Test
	public void testMultiThreadRun_testSkipValidationToo() throws IOException {
		long start = System.currentTimeMillis();

		Converter.main(new String[]{Converter.SKIP_VALIDATION,
				"src/test/resources/pathTest/a.xml",
				"src/test/resources/pathTest/subdir/*.xml"});

		long finish = System.currentTimeMillis();

		Path aJson = Paths.get("a.qpp.json");
		Path dJson = Paths.get("d.qpp.json");

		// a.qpp.json and d.qpp.json will not exist because the a.xml and d.xml
		// file will get validation
		assertTrue( Files.exists(aJson) );
		assertTrue( Files.exists(dJson) );

		Files.delete(aJson);
		Files.delete(dJson);

		System.out.println("Time to run two thread transform " + (finish - start));
	}

}
