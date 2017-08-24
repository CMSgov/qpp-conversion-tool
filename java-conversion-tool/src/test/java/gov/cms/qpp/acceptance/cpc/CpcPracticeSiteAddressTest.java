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

public class CpcPracticeSiteAddressTest {
	private static final Path DIR = Paths.get("src/test/resources/cpc_plus/");


	@Test
	public void practiceSiteAddressExampleOneFileSuccess() throws IOException {
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(DIR, "*-success.xml")) {
			for (Path entry: stream) {
				Converter converter = new Converter(new PathQrdaSource(entry));
				converter.transform();
			}
		} catch (TransformException ex) {
c			Assert.fail("Should not fail conversion");
		}
	}
}
