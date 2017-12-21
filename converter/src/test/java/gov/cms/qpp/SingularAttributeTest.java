package gov.cms.qpp;

import gov.cms.qpp.acceptance.helper.MarkupManipulator;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.correlation.model.Goods;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataPayerDecoder;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertWithMessage;

class SingularAttributeTest {

	private static final String NAMESPACE_URI = "urn:hl7-org:v3";
	private static Map<String, Goods> corrMap;
	private static Set<String> exclusions;
	private static int inclusionCount = 0;
	private static MarkupManipulator manipulator;


	@BeforeAll
	@SuppressWarnings("unchecked")
	static void before() throws NoSuchFieldException, IllegalAccessException,
			IOException, SAXException, ParserConfigurationException {
		manipulator = new MarkupManipulator.MarkupManipulatorBuilder()
				.setPathname("../qrda-files/valid-QRDA-III-latest.xml")
				.setNsAware(true)
				.build();

		Field corrMapField = PathCorrelator.class.getDeclaredField("pathCorrelationMap");
		corrMapField.setAccessible(true);
		corrMap = (Map<String, Goods>) corrMapField.get(null);

		exclusions = new HashSet<>(
				Arrays.asList(
						//MultipleTinsDecoder maps multiple tin/npi combination
						ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
						ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER,
						//There are no validations currently for entity type
						ClinicalDocumentDecoder.ENTITY_ID,
						ClinicalDocumentDecoder.PRACTICE_SITE_ADDR,
						PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE,
						PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE,
						//There are no validations for performanceYear
						ReportingParametersActDecoder.PERFORMANCE_YEAR,
						SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_CODE,
						SupplementalDataPayerDecoder.SUPPLEMENTAL_DATA_PAYER_CODE,
						//stratum is not currently mapped
						"stratum")
		);

		corrMap.keySet().forEach(key -> {
			String[] components = key.split(PathCorrelator.KEY_DELIMITER);
			if (!exclusions.contains(components[1])) {
				inclusionCount++;
			}
		});
	}

	//TODO: look into ENTITY_TYPE w/ multiple tin example
	//TODO: Exempt
	// MultipleTinsDecoder TAX_PAYER_IDENTIFICATION_NUMBER
	// MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER
	// due to ugliness with multiple tin decoding

	@Test
	void blanketDoubleUp() {
		assertWithMessage("failed duplication scenarios should equal the inclusion count")
				.that(blanketCheck(false))
				.isEqualTo(inclusionCount);
	}

	@Test
	void blanketRemoval() {
		assertWithMessage("failed removal scenarios should equal the inclusion count")
				.that(blanketCheck(true))
				.isEqualTo(inclusionCount);
	}

	private int blanketCheck(boolean remove) {
		int errorCount = 0;
		for (String key : corrMap.keySet()) {
			String[] components = key.split(PathCorrelator.KEY_DELIMITER);
			if (!exclusions.contains(components[1])) {
				List<Detail> details = executeScenario(components[0], components[1], remove);

				if (!details.isEmpty()) {
					errorCount++;
				}
				assertWithMessage("Combination of: " + components[0] + " and " +
						components[1] + " should be unique.").that(details.size())
						.isGreaterThan(0);
			}
		}
		return errorCount;
	}

	@Test
	void doubleUpProgramName() {
		List<Detail> details = executeScenario(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME, false);

		assertWithMessage("error should be about missing missing program name").that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME);

		assertWithMessage("error should be about missing program name").that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME);
	}

	@Test
	void noProgramName() {
		List<Detail> details = executeScenario(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME, true);

		assertWithMessage("error should be about missing program name").that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME,
						ErrorCode.CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME);
	}

	private List<Detail> executeScenario(String templateId, String attribute, boolean remove) {
		String xPath = getPath(templateId, attribute);
		InputStream inStream = manipulator.upsetTheNorm(xPath, remove);
		Converter converter = new Converter(
				new InputStreamSupplierSource(xPath, () -> inStream));
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			return errors.getErrors().stream().map(Error::getDetails).flatMap(List::stream).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private String getPath(String templateId, String attribute) {
		String path = PathCorrelator.getXpath(templateId, attribute, NAMESPACE_URI);
		if (path == null) {
			System.out.println("Bad combo templateId: " + templateId + " attribute: " + attribute);
		}
		return "//" + path;
	}

}
