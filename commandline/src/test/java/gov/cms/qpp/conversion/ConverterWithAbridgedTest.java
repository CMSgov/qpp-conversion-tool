package gov.cms.qpp.conversion;

import org.junit.jupiter.api.AfterEach;

import gov.cms.qpp.test.jimfs.JimfsContract;
import gov.cms.qpp.test.jimfs.JimfsTest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.truth.Truth.assertWithMessage;

class ConverterWithAbridgedTest implements JimfsContract {

	private FileSystem fileSystem;
	private Field fileSystemField;
	private FileSystem defaultFileSystem;

	@AfterEach
	void teardown() throws IOException, IllegalArgumentException, IllegalAccessException {
		fileSystemField.set(null, defaultFileSystem);
		fileSystem.close();
	}

	@JimfsTest
	void testWithAbridgedXml(FileSystem fileSystem) throws Exception {
		setup(fileSystem);

		String fileName = "valid-QRDA-III-abridged.qpp.json";
		ConversionEntry.main("--" + ConversionEntry.SKIP_VALIDATION,
				"src/test/resources/valid-QRDA-III-abridged.xml");

		Path aJson = fileSystem.getPath(fileName);

		assertWithMessage("File %s should exist", fileName)
				.that(Files.exists(aJson))
				.isTrue();

		Files.delete(aJson);
	}

	@JimfsTest
	void testMultiThreadRun_testSkipValidationToo(FileSystem fileSystem) throws Exception {
		setup(fileSystem);

		String aConversion = "a.qpp.json";
		String dConversion = "d.qpp.json";

		ConversionEntry.main("--" + ConversionEntry.SKIP_VALIDATION,
				"src/test/resources/pathTest/a.xml",
				"src/test/resources/pathTest/subdir/*.xml");

		Path aJson = fileSystem.getPath(aConversion);
		Path dJson = fileSystem.getPath(dConversion);

		// a.qpp.json and d.qpp.json will not exist because the a.xml and d.xml
		// file will get validation
		assertWithMessage("File %s should exist", aConversion)
				.that(Files.exists(aJson)).isTrue();
		assertWithMessage("File %s should exist", dConversion)
				.that(Files.exists(dJson)).isTrue();

		Files.delete(aJson);
		Files.delete(dJson);
	}

	private void setup(FileSystem fileSystem) throws Exception {
		this.fileSystem = fileSystem;
		fileSystemField = ConversionEntry.class.getDeclaredField("fileSystem");
		fileSystemField.setAccessible(true);
		defaultFileSystem = (FileSystem) fileSystemField.get(null);
		fileSystemField.set(null, fileSystem);
	}

}
