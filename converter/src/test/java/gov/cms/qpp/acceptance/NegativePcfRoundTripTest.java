package gov.cms.qpp.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;


/**
 * Negative Testing Scenarios to check PCF for invalid top level attributes and validations
 * Ensures:
 * - Errors caught for invalid apm entity, cehrt id, performance period, missing required measure ids
 */
public class NegativePcfRoundTripTest {
	static final Path Y5_NEGATIVE_PCF = Paths.get("src/test/resources/pcf/failure/2021/Y5_Negative_PCF_Sample_QRDA-III.xml");
	ApmEntityIds apmEntityIds;

	private static final String[] EXPECTED_PCF_REQUIRED_MEASURES = {
			"2c928085-7198-38ee-0171-9d78a0d406b3", // 122v9
			"2c928085-7198-38ee-0171-9d6e026b066b", // 130v9
			"2c928085-7198-38ee-0171-9da6456007ab"  // 165v9
	};

	@BeforeEach
	void setup() {
		apmEntityIds = new ApmEntityIds("test_apm_entity_ids.json", "test_apm_entity_ids.json");
	}

	@AfterEach
	void teardown() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	void testPcfMissingRequiredMeasureId() {
		List<Detail> details = conversionError(Y5_NEGATIVE_PCF);
		LocalizedProblem error = ProblemCode.PCF_TOO_FEW_QUALITY_MEASURE_CATEGORY.format(3,
			String.join(",", EXPECTED_PCF_REQUIRED_MEASURES));

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(error);
	}

	@Test
	void testPcfInvalidApm() {
		List<Detail> details = conversionError(Y5_NEGATIVE_PCF);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_INVALID_APM.getProblemCode());
	}

	@Test
	void testPcfInvalidCehrtId() {
		List<Detail> details = conversionError(Y5_NEGATIVE_PCF);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PCF_MISSING_CEHRT_ID.
				format(ClinicalDocumentDecoder.PCF_PROGRAM_NAME.toUpperCase()));
	}

	@Test
	void testPcfInvalidPerformancePeriod() {
		List<Detail> details = conversionError(Y5_NEGATIVE_PCF);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PCF_PERFORMANCE_PERIOD_START.format(ClinicalDocumentDecoder.PCF_PROGRAM_NAME.toUpperCase(Locale.ROOT)));

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PCF_PERFORMANCE_PERIOD_END.format(ClinicalDocumentDecoder.PCF_PROGRAM_NAME.toUpperCase(Locale.ROOT)));
	}

	List<Detail> conversionError(Path path) {
		Converter converter = new Converter(new PathSource(path), new Context(apmEntityIds));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		return details;
	}
}
