package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsMessageEquals;
import gov.cms.qpp.conversion.util.JsonHelper;
import gov.cms.qpp.conversion.validate.MipsQualityMeasureIdValidator;

import java.util.ArrayList;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class QualityMeasureIdRoundTripTest {
	public static final Path JUNK_QRDA3_FILE = Paths.get("src/test/resources/negative/junk_in_quality_measure.xml");
	public static final Path INVALID_PERFORMANCE_UUID_FILE =
			Paths.get("src/test/resources/negative/mipsInvalidPerformanceRateUuid.xml");

	@Test
	public void testRoundTripForQualityMeasureId() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='quality')].measurements[*]", List.class);

		assertWithMessage("There should still be a quality measure even with the junk stuff in quality measure.")
				.that(qualityMeasures)
				.hasSize(1);
		assertWithMessage("The measureId in the quality measure should still populate given the junk stuff in the measure.")
				.that(qualityMeasures.get(0).get("measureId"))
				.isEqualTo("236");
	}

	@Test
	public void testMeasureCMS68v6PerformanceRateUuid() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(INVALID_PERFORMANCE_UUID_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		String measureId = "CMS68v6";
		String numerUuid = "EFFE261C-0D57-423E-992C-7141B132768C";

		String message = String.format(MipsQualityMeasureIdValidator.INCORRECT_UUID, measureId,
				PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID, numerUuid);
		assertWithMessage("Must contain the proper error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.contains(message);
	}

	@Test
	public void testMeasureCMS160v5PerformanceRateUuid() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(INVALID_PERFORMANCE_UUID_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		String measureId = "CMS160v5";
		String numerUuid = "33538979-8425-45A4-B724-D74CC0A84EF3";

		String message = String.format(MipsQualityMeasureIdValidator.INCORRECT_UUID, measureId,
				PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID, numerUuid);
		assertWithMessage("Must contain the proper error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.contains(MipsQualityMeasureIdValidator.SINGLE_PERFORMANCE_RATE);
		assertWithMessage("Must contain the proper error")
				.that(details).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.contains(message);
	}
}
