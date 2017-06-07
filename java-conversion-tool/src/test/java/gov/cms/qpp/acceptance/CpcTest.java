package gov.cms.qpp.acceptance;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;

import gov.cms.qpp.conversion.ConversionEntry;

public class CpcTest {

	private static final String CPC_FILE = "src/test/resources/cpc1.xml";
	private static final Path SUCCESS = Paths.get("cpc1.qpp.json");
	private static final Path ERROR = Paths.get("cpc1.err.json");

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(SUCCESS);
		Files.deleteIfExists(ERROR);
	}

	@Test
	public void historicalAciSectionScope() throws Exception {
		run("ACI_SECTION");
		assertSuccess();
	}

	@Test
	public void historicalIaSectionScope() throws Exception {
		run("IA_SECTION");
		assertSuccess();
	}

	@Test
	public void historicalIAMeasurePerformedScope() throws Exception {
		run("IA_MEASURE");
		assertSuccess();
	}

	@Test
	public void historicalClinicalDocumentScope() throws Exception {
		run("CLINICAL_DOCUMENT");
		assertError();
	}

	private void run(String type) {
		ConversionEntry.main("-t", type, "-b", CPC_FILE);
	}

	private void assertSuccess() {
		assertTrue(Files.exists(SUCCESS));
	}

	private void assertError() {
		assertTrue(Files.exists(ERROR));
	}

}