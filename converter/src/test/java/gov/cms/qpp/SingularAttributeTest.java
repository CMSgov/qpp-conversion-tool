package gov.cms.qpp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.correlation.model.Goods;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.decode.QualitySectionDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder;
import gov.cms.qpp.conversion.decode.SupplementalDataPayerDecoder;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.validate.ClinicalDocumentValidator;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

class SingularAttributeTest{

	private static Map<String, Goods> corrMap;
	private static Set<String> exclusions;
	private static int inclusionCount = 0;
	private static MarkupManipulationHandler manipulationHandler;


	@BeforeAll
	@SuppressWarnings("unchecked")
	static void before() throws NoSuchFieldException, IllegalAccessException {
		manipulationHandler = new MarkupManipulationHandler("../qrda-files/valid-QRDA-III-latest.xml");

		Field corrMapField = PathCorrelator.class.getDeclaredField("pathCorrelationMap");
		corrMapField.setAccessible(true);
		corrMap = (Map<String, Goods>) corrMapField.get(null);

		exclusions = new HashSet<>(
				Arrays.asList(
						//MultipleTinsDecoder maps multiple tin/npi combination
						ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER,
						ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER,
						ClinicalDocumentDecoder.ENTITY_ID,
						//There are no validations currently for entity type
						ClinicalDocumentDecoder.PRACTICE_ID,
						ClinicalDocumentDecoder.PRACTICE_SITE_ADDR,
						//We have validations implemented
						ClinicalDocumentDecoder.PCF_ENTITY_ID,
						ClinicalDocumentDecoder.VG_ID,
						ClinicalDocumentDecoder.APM_ENTITY_ID,
						PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE,
						PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE,
						//There are no validations for performanceYear
						ReportingParametersActDecoder.PERFORMANCE_YEAR,
						SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_CODE,
						SupplementalDataPayerDecoder.SUPPLEMENTAL_DATA_PAYER_CODE,
						QualitySectionDecoder.CATEGORY_SECTION_V4,
						//stratum is not currently mapped
						"stratum",
						"cehrtId",
						"performer")
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
				List<Detail> details = manipulationHandler.executeScenario(components[0], components[1], remove);

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
		List<Detail> details = manipulationHandler.executeScenario(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME, false);

		assertWithMessage("error should be about missing missing program name").that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(ClinicalDocumentValidator.VALID_PROGRAM_NAMES));

		assertWithMessage("error should be about missing program name").that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(ClinicalDocumentValidator.VALID_PROGRAM_NAMES));
	}

	@Test
	void noProgramName() {
		List<Detail> details = manipulationHandler.executeScenario(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME, true);

		assertWithMessage("error should be about missing program name").that(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME.format(ClinicalDocumentValidator.VALID_PROGRAM_NAMES));
	}
}
