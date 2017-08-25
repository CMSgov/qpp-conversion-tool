package gov.cms.qpp.acceptance.cpc;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.TransformException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

public class CpcPlusRoundTripTest {

	private static final Path DIR = Paths.get("src/test/resources/cpc_plus/");

	@Test
	public void cpcPlusFileSuccesses() throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIR, "*-success.xml")) {
			for (Path entry: stream) {
				Converter converter = new Converter(new PathQrdaSource(entry));
				converter.transform();
			}
		} catch (TransformException ex) {
			Assert.fail("Should not fail conversion");
		}
	}

	@Test
	public void cpcPlusFileFailures() throws IOException {
		DirectoryStream<Path> stream = Files.newDirectoryStream(DIR, "*-failure.xml");
		for (Path entry: stream) {
			Converter converter = new Converter(new PathQrdaSource(entry));
			try {
				converter.transform();
				Assert.fail("Should contain validation errors");
			} catch (TransformException ex) {
				//Good stuff
			}
		}

	}
}
