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
import java.util.HashSet;
import java.util.List;

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
		Files.deleteIfExists(Paths.get("angerTheConverter.err.json"));
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
				FIXTURES.get(testSection), getScoped(content).get(0));
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
				FIXTURES.get(testSection), getScoped(content).get(0));
	}

	@Test
	public void testScopedAciAggregateCountConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_AGGREGATE_COUNT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	@Test
	public void testScopedAciNumeratorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_NUMERATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	@Test
	public void testScopedAciDenominatorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_DENOMINATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	@Test
	public void testScopedAciNumeratorDenominatorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_NUMERATOR_DENOMINATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	@Test
	public void testFullScopeConversion() throws IOException {
		//setup
		String testSection = TemplateId.CLINICAL_DOCUMENT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/valid-QRDA-III.xml");

		HashMap<String,Object> content = readJson("valid-QRDA-III.qpp.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), content);
	}

	//negative
	@Test
	public void testNegativeFullScopeConversion() throws IOException {
		//setup
		String testSection = TemplateId.CLINICAL_DOCUMENT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/negative/angerTheConverter.xml");

		HashMap<String,Object> content = readJson("angerTheConverter.err.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				8, getErrors(content).size());
	}

	@Test
	public void testNegativeAciNumeratorDenominatorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_NUMERATOR_DENOMINATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/negative/angerTheConverter.xml");

		HashMap<String, Object> content = readJson("angerTheConverter.err.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				7, getErrors(content).size());
	}

	@Test
	public void testNegativeIaSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.IA_SECTION.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/negative/angerTheConverter.xml");

		HashMap<String, Object> content = readJson("angerTheConverter.err.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				1, getErrors(content).size());
	}

	@Test
	public void testNegativeAciAggregateCountConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_AGGREGATE_COUNT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection,
				"src/test/resources/negative/angerTheConverter.xml");

		HashMap<String, Object> content = readJson("angerTheConverter.err.json");

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				3, getErrors(content).size());
	}



	private List<?> getErrors(HashMap<String,Object> content) {
		return (List<?>) ((HashMap<String, ?>) ((List<?>) content.get("errorSources")).get(0)).get("validationErrors");
	}

	private List<?> getScoped(HashMap<String,Object> content) {
		return (List<?>) content.get("scoped");
	}
}
