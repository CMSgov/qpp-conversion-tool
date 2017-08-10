package gov.cms.qpp.conversion;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Sets;
import gov.cms.qpp.BaseTest;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
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

	private static final String SUCCESS_MAKER = "../qrda-files/valid-QRDA-III-latest.xml";
	private static final String ERROR_MAKER = "src/test/resources/negative/angerTheConverter.xml";

	private static Map<String, Object> fixtures;

	/**
	 * Load fixture json for use as a baseline for expected scoped conversion outcomes.
	 *
	 * @throws IOException
	 */
	@BeforeClass
	public static void loadFixtures() throws IOException {
		fixtures = readJson(Paths.get("src/test/resources/converter/scopedConversionFixture.json"), HashMap.class);
	}

	/**
	 * Verify CMS V2 Measure Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedV2MeasureSectionConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.MEASURE_SECTION_V2.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content).get(0));
	}

	/**
	 * Verify CMS V2 Measure Reference Results conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedCmsV2MeasureReferenceResultsConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content));
	}

	/**
	 * Verify CMS V2 Measure Data conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedV2MeasureDataConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.MEASURE_DATA_CMS_V2.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content));
	}

	/**
	 * Verify ACI Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciSectionConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_SECTION.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content).get(0));
	}


	/**
	 * Verify IA Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedIaSectionConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.IA_SECTION.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content).get(0));
	}

	/**
	 * Verify ACI Aggregate Count conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciAggregateCountConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_AGGREGATE_COUNT.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content));
	}

	/**
	 * Verify ACI Numerator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciNumeratorConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_NUMERATOR.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content));
	}

	/**
	 * Verify ACI Denominator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciDenominatorConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_DENOMINATOR.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content));
	}

	/**
	 * Verify ACI Numerator Denominator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testScopedAciNumeratorDenominatorConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_NUMERATOR_DENOMINATOR.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), getScoped(content));
	}

	/**
	 * Verify Clinical Document conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testFullScopeConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.CLINICAL_DOCUMENT.name());

		//when
		Converter converter = new Converter(Paths.get(SUCCESS_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		JsonWrapper qpp = converter.transform();
		Map<String, Object> content = JsonHelper.readJson(qpp.toString(), HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				fixtures.get(testSection.name()), content);
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
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.CLINICAL_DOCUMENT.name());

		//when
		Converter converter = new Converter(Paths.get(ERROR_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		String errors = "";
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			ObjectWriter jsonObjectWriter = new ObjectMapper()
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.writer()
				.withDefaultPrettyPrinter();
			errors = jsonObjectWriter.writeValueAsString(exception.getDetails());
		}
		Map<String, Object> content = JsonHelper.readJson(errors, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				7, getErrors(content).size());
	}

	/**
	 * Verify failure for attempted invalid ACI Numerator Denominator conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeAciNumeratorDenominatorConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_NUMERATOR_DENOMINATOR.name());

		//when
		Converter converter = new Converter(Paths.get(ERROR_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		String errors = "";
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			ObjectWriter jsonObjectWriter = new ObjectMapper()
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.writer()
				.withDefaultPrettyPrinter();
			errors = jsonObjectWriter.writeValueAsString(exception.getDetails());
		}
		Map<String, Object> content = JsonHelper.readJson(errors, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				5, getErrors(content).size());
	}

	/**
	 * Verify failure for attempted invalid IA Section conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeIaSectionConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.IA_SECTION.name());

		//when
		Converter converter = new Converter(Paths.get(ERROR_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		String errors = "";
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			ObjectWriter jsonObjectWriter = new ObjectMapper()
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.writer()
				.withDefaultPrettyPrinter();
			errors = jsonObjectWriter.writeValueAsString(exception.getDetails());
		}
		Map<String, Object> content = JsonHelper.readJson(errors, HashMap.class);

		//then
		assertEquals("content should match valid " + testSection + " fixture",
				2, getErrors(content).size());
	}

	/**
	 * Verify failure for attempted invalid ACI Aggregate Count conversion
	 *
	 * @throws IOException
	 */
	@Test
	public void testNegativeAciAggregateCountConversion() throws IOException {
		//setup
		QrdaScope testSection = QrdaScope.getInstanceByName(TemplateId.ACI_AGGREGATE_COUNT.name());

		//when
		Converter converter = new Converter(Paths.get(ERROR_MAKER));
		Converter.setScope(Sets.newHashSet(testSection));
		String errors = "";
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			ObjectWriter jsonObjectWriter = new ObjectMapper()
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.writer()
				.withDefaultPrettyPrinter();
			errors = jsonObjectWriter.writeValueAsString(exception.getDetails());
		}
		Map<String, Object> content = JsonHelper.readJson(errors, HashMap.class);

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
		return (List<?>) ((Map<String, ?>) ((List<?>) content.get("errors")).get(0)).get("details");
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
