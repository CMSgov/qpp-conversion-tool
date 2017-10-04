package gov.cms.qpp.acceptance.cpc;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CpcPlusRoundTripTest {

	private static final Path DIR = Paths.get("src/test/resources/cpc_plus/");

	@Test
	public void testCpcPlusFileSuccesses() throws IOException {
		Map<Path, AllErrors> errors = new HashMap<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIR, "*-success.xml")) {
			for (Path entry : stream) {
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
				} catch (TransformException failure) {
					errors.put(entry, failure.getDetails());
				}
			}
		}

		if (!errors.isEmpty()) {
			Assert.fail("Failed cpc plus conversions: " + errors);
		}
	}

	@Test
	public void testCpcPlusFileFailures() throws IOException {
		List<Path> successesThatShouldBeErrors = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIR, "*-failure.xml")) {
			for (Path entry : stream) {
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
					successesThatShouldBeErrors.add(entry);
				} catch (TransformException expected) {
					System.out.println();
				}
			}
		}

		if (!successesThatShouldBeErrors.isEmpty()) {
			Assert.fail("Succeeded in cpc plus conversions that should have failed: " + successesThatShouldBeErrors);
		}
	}

	@Test
	public void testCpcPlusFilesAreAllChecked() throws IOException {
		long invalidFiles = Files.list(DIR).filter(file -> {
			String fileName = file.toString();

			return fileName.endsWith(".xml") && !fileName.endsWith("-failure.xml") && !fileName.endsWith("-success.xml");
		}).count();

		Assert.assertEquals(0, invalidFiles);
	}
}
