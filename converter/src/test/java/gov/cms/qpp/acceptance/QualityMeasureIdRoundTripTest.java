package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.util.JsonHelper;

class QualityMeasureIdRoundTripTest {
	static final Path JUNK_QRDA3_FILE = Paths.get("src/test/resources/negative/junk_in_quality_measure.xml");
	static final Path INVALID_PERFORMANCE_UUID_FILE =
			Paths.get("src/test/resources/negative/mipsInvalidPerformanceRateUuid.xml");
	static final Path INSENSITIVE_TEXT_FILE =
			Paths.get("src/test/resources/fixtures/textInsensitiveQualityMeasureUuids.xml");

	@Test
	void testRoundTripForQualityMeasureId() throws IOException {
		Converter converter = new Converter(new PathSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='quality')].measurements[*]", List.class);

		assertThat(qualityMeasures).hasSize(1);
		assertWithMessage("The measureId in the quality measure should still populate given the junk stuff in the measure.")
				.that(qualityMeasures.get(0).get("measureId"))
				.isEqualTo("236");
	}

	@Test
	void testMeasureCMS68v6PerformanceRateUuid() throws IOException {
		Converter converter = new Converter(new PathSource(INVALID_PERFORMANCE_UUID_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		String measureId = "CMS68v6";
		String incorrectId = "00000000-0000-0000-0000-1NV4L1D";

		LocalizedError error = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(measureId,
				PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID, incorrectId);
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(error);
	}

	@Test
	void testMeasureCMS160v5PerformanceRateUuid() throws IOException {
		Converter converter = new Converter(new PathSource(INVALID_PERFORMANCE_UUID_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE);
	}

	@Test
	void testMeasureCMS52v5WithInsensitiveTextUuid() throws IOException {
		Converter converter = new Converter(new PathSource(INSENSITIVE_TEXT_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.doesNotContain(ErrorCode.MEASURE_GUID_MISSING);
	}

	@Test
	void testMeasureCMS52v5InsensitiveMeasureDataUuid() throws IOException {
		Converter converter = new Converter(new PathSource(INSENSITIVE_TEXT_FILE));
		List<Detail> details = new ArrayList<>();

		LocalizedError error = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(
				"CMS52v5", "DENOM", "04BF53CE-6993-4EA2-BFE5-66E36172B388");

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.doesNotContain(error);
	}
}
