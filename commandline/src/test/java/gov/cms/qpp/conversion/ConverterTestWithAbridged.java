package gov.cms.qpp.conversion;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.cms.qpp.test.FileTestHelper;

public class ConverterTestWithAbridged {

	private static Field fileSystemField;
	private static FileSystem defaultFileSystem;
	private static FileSystem fileSystem;

	@BeforeClass
	public static void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		fileSystem = FileTestHelper.createMockFileSystem();
		fileSystemField = ConversionEntry.class.getDeclaredField("fileSystem");
		fileSystemField.setAccessible(true);
		defaultFileSystem = (FileSystem) fileSystemField.get(null);
		fileSystemField.set(null, fileSystem);
	}

	@AfterClass
	public static void teardown() throws IOException, IllegalArgumentException, IllegalAccessException {
		fileSystemField.set(null, defaultFileSystem);
		fileSystem.close();
	}

	@Test
	public void testWithAbridgedXml() throws IOException {
		long start = System.currentTimeMillis();

		ConversionEntry.main("--" + ConversionEntry.SKIP_VALIDATION,
				"src/test/resources/valid-QRDA-III-abridged.xml");

		long finish = System.currentTimeMillis();

		Path aJson = fileSystem.getPath("valid-QRDA-III-abridged.qpp.json");

		assertTrue(Files.exists(aJson));

		Files.delete(aJson);

		System.out.println("Time to run transform " + (finish - start));
	}

	@Test
	public void testMultiThreadRun_testSkipValidationToo() throws IOException {
		long start = System.currentTimeMillis();

		ConversionEntry.main("--" + ConversionEntry.SKIP_VALIDATION,
				"src/test/resources/pathTest/a.xml",
				"src/test/resources/pathTest/subdir/*.xml");

		long finish = System.currentTimeMillis();

		Path aJson = fileSystem.getPath("a.qpp.json");
		Path dJson = fileSystem.getPath("d.qpp.json");

		// a.qpp.json and d.qpp.json will not exist because the a.xml and d.xml
		// file will get validation
		assertTrue( Files.exists(aJson) );
		assertTrue( Files.exists(dJson) );

		Files.delete(aJson);
		Files.delete(dJson);

		System.out.println("Time to run two thread transform " + (finish - start));
	}

}
