package gov.cms.qpp.conversion;

import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static gov.cms.qpp.util.JsonHelper.readJson;
import static junit.framework.TestCase.assertEquals;

public class ScopedConversionTest extends BaseTest {

	private static final String TEMPLATE_SCOPE = "--" + ConversionEntry.TEMPLATE_SCOPE;
	private static HashMap<String,Object> FIXTURES;

	@BeforeClass
	public static void loadFixtures() throws IOException {
		FIXTURES = readJson("src/test/resources/converter/scopedConversionFixture.json");
	}

	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
	}

	@Test
	public void testScopedAciSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_SECTION.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), content);
	}


	@Test
	public void testScopedIaSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.IA_SECTION.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), content);
	}

}
