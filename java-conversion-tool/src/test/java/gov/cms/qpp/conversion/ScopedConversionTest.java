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
import java.util.List;
import java.util.Map;

import static gov.cms.qpp.conversion.util.JsonHelper.readJson;
import static junit.framework.TestCase.assertEquals;

/**
 * Verify scoped conversions
 */
public class ScopedConversionTest extends BaseTest {

	private static final String SUCCESS_MAKER = "src/test/resources/valid-QRDA-III.xml";
	private static final String ERROR_MAKER = "src/test/resources/negative/angerTheConverter.xml";
	private static final String SUCCESS_FILE = "valid-QRDA-III.qpp.json";
	private static final String ERROR_FILE = "angerTheConverter.err.json";

	private static final String TEMPLATE_SCOPE = "--" + ConversionEntry.TEMPLATE_SCOPE;
	private static Map<String, Object> FIXTURES;

	/**
	 * Load fixture json for use as a baseline for expected scoped conversion outcomes.
	 *
	 * @throws IOException
	 */
	@BeforeClass
	public static void loadFixtures() throws IOException {
		FIXTURES = readJson("src/test/resources/converter/scopedConversionFixture.json", HashMap.class);
	}

	/**
	 * Clean up conversion output files.
	 *
	 * @throws IOException
	 */
	@After
	public void cleanup() throws IOException {
		Files.deleteIfExists(Paths.get("valid-QRDA-III.qpp.json"));
		Files.deleteIfExists(Paths.get("angerTheConverter.err.json"));
	}

	/**
	 * Verify CMS V2 Measure Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedV2MeasureSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.MEASURE_SECTION_V2.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content).get(0));
	}

	/**
	 * Verify CMS V2 Measure Reference Results conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedCmsV2MeasureReferenceResultsConversion() throws IOException {
		//setup
		String testSection = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	/**
	 * Verify CMS V2 Measure Data conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedV2MeasureDataConversion() throws IOException {
		//setup
		String testSection = TemplateId.MEASURE_DATA_CMS_V2.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	/**
	 * Verify ACI Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_SECTION.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content).get(0));
	}


	/**
	 * Verify IA Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedIaSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.IA_SECTION.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content).get(0));
	}

	/**
	 * Verify ACI Aggregate Count conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciAggregateCountConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_AGGREGATE_COUNT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	/**
	 * Verify ACI Numerator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciNumeratorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_NUMERATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	/**
	 * Verify ACI Denominator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciDenominatorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_DENOMINATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	/**
	 * Verify ACI Numerator Denominator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciNumeratorDenominatorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_NUMERATOR_DENOMINATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), getScoped(content));
	}

	/**
	 * Verify Clinical Document conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testFullScopeConversion() throws IOException {
		//setup
		String testSection = TemplateId.CLINICAL_DOCUMENT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, SUCCESS_MAKER);
		Map<String, Object> content = readJson(SUCCESS_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				FIXTURES.get(testSection), content);
	}

	//negative

	/**
	 * Verify failure for attempted invalid Clinical Document conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeFullScopeConversion() throws IOException {
		//setup
		String testSection = TemplateId.CLINICAL_DOCUMENT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, ERROR_MAKER);
		Map<String, Object> content = readJson(ERROR_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				5, getErrors(content).size());
	}

	/**
	 * Verify failure for attempted invalid ACI Numerator Denominator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeAciNumeratorDenominatorConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_NUMERATOR_DENOMINATOR.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, ERROR_MAKER);
		Map<String, Object> content = readJson(ERROR_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				4, getErrors(content).size());
	}

	/**
	 * Verify failure for attempted invalid IA Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeIaSectionConversion() throws IOException {
		//setup
		String testSection = TemplateId.IA_SECTION.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, ERROR_MAKER);
		Map<String, Object> content = readJson(ERROR_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				1, getErrors(content).size());
	}

	/**
	 * Verify failure for attempted invalid ACI Aggregate Count conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeAciAggregateCountConversion() throws IOException {
		//setup
		String testSection = TemplateId.ACI_AGGREGATE_COUNT.name();

		//when
		ConversionEntry.main(TEMPLATE_SCOPE, testSection, ERROR_MAKER);
		Map<String, Object> content = readJson(ERROR_FILE, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				3, getErrors(content).size());
	}

	/**
	 * Helper for retrieving validation errors
	 *
	 * @param content hash representing conversion error output
	 * @return list of validation errors
	 */
	@SuppressWarnings("unchecked")
	private List<?> getErrors(Map<String, Object> content) {
		return (List<?>) ((Map<String, ?>) ((List<?>) content.get("errorSources")).get(0)).get("validationErrors");
	}

	/**
	 * Helper for retrieving scoped conversion content
	 *
	 * @param content hash representing valid conversion output
	 * @return list of converted hashes / lists
	 */
	private List<?> getScoped(Map<String, Object> content) {
		return (List<?>) content.get("scoped");
	}
}
