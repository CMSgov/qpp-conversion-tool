package gov.cms.qpp.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
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
		"40280382-6963-bf5e-0169-da3833273869",
		"40280382-6963-bf5e-0169-da566ea338a5",
		"40280382-6963-bf5e-0169-da5e74be38bf"
	};

	@BeforeEach
	void setup() {
		apmEntityIds = new ApmEntityIds("test_apm_entity_ids.json");
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
			.contains(ProblemCode.CPC_PCF_MISSING_CEHRT_ID.getProblemCode());
	}

	@Test
	void testPcfInvalidPerformancePeriod() {
		List<Detail> details = conversionError(Y5_NEGATIVE_PCF);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PCF_PERFORMANCE_PERIOD_START.getProblemCode());

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PCF_PERFORMANCE_PERIOD_END.getProblemCode());
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
