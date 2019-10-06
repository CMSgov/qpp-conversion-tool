package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.TypeRef;

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
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.util.JsonHelper;

class QualityMeasureIdRoundTripTest {
	static final Path JUNK_QRDA3_FILE = Paths.get("src/test/resources/negative/junk_in_quality_measure.xml");
	static final Path INVALID_PERFORMANCE_UUID_FILE =
			Paths.get("src/test/resources/negative/mipsInvalidPerformanceRateUuid.xml");
	static final Path INSENSITIVE_TEXT_FILE =
			Paths.get("src/test/resources/fixtures/textInsensitiveQualityMeasureUuids.xml");
	static final Path CORRECT_MULTI_TO_SINGLE_PERF_RATE_FILE =
		Paths.get("src/test/resources/correctMultiToSinglePerfMeasureExample.xml");
	static final Path INCORRECT_MULTI_TO_SINGLE_PERF_RATE_FILE =
		Paths.get("src/test/resources/negative/wrongSubPopulationsMeasure135.xml");
	static final Path MISSING_COUNT_FOR_PERF_DENOM =
		Paths.get("src/test/resources/negative/perfDenomAggCountMissing.xml");

	@Test
	void testRoundTripForQualityMeasureId() {
		Converter converter = new Converter(new PathSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='quality')].measurements[*]", new TypeRef<List<Map<String, ?>>>() { });

		assertThat(qualityMeasures).hasSize(1);
		assertWithMessage("The measureId in the quality measure should still populate given the junk stuff in the measure.")
				.that(qualityMeasures.get(0).get("measureId"))
				.isEqualTo("236");
	}

	@Test
	void testMeasureCMS165DoesNotContainUnexpectedValue() {
		Converter converter = new Converter(new PathSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();
		List<String> containsUnwantedValueList = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='quality')].measurements[0].value.value", new TypeRef<List<String>>() { });
		List<String> measureId = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='quality')].measurements[*].measureId", new TypeRef<List<String>>() { });

		assertThat(measureId.get(0)).isEqualTo("236");
		assertThat(containsUnwantedValueList).isEmpty();
	}

	@Test
	void testMeasureCMS68v8PerformanceRateUuid() {
		Converter converter = new Converter(new PathSource(INVALID_PERFORMANCE_UUID_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		String measureId = "CMS68v8";
		String correctId = MeasureConfigs.getConfigurationMap()
			.get("40280382-5fa6-fe85-0160-0ea3e0012376").getSubPopulation().get(0).getNumeratorUuid();

		LocalizedError error = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(measureId,
				PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID, correctId);
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(error);
	}

	@Test
	void testMeasureCMS138v7PerformanceRateUuid() {
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
	void testMeasureCMS52v5WithInsensitiveTextUuid() {
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
	void testMeasureCMS52v5InsensitiveMeasureDataUuid() {
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

	@Test
	void testCorrectMultiToSinglePerfMeasureExample() {
		Converter converter = new Converter(new PathSource(CORRECT_MULTI_TO_SINGLE_PERF_RATE_FILE));
		JsonWrapper qpp = converter.transform();

		String cms135MeasureId= "005";

		List<String> measureIds = JsonHelper.readJsonAtJsonPath(qpp.toString(),
			"$.measurementSets[?(@.category=='quality')].measurements[*].measureId", new TypeRef<List<String>>() { });
		assertThat(measureIds).contains(cms135MeasureId);
	}

	@Test
	void testIncorrectMultiToSinglePerfMeasureExample() {
		Converter converter = new Converter(new PathSource(INCORRECT_MULTI_TO_SINGLE_PERF_RATE_FILE));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat(details.size()).isEqualTo(2);
		for(Detail detail: details) {
			assertThat(detail.getErrorCode()).isEqualTo(59);
		}
	}

	@Test
	void testMissingPerfDenomAggregateCount() {
		Converter converter = new Converter(new PathSource(MISSING_COUNT_FOR_PERF_DENOM));
		List<Detail> details = new ArrayList<>();

		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		String populationId = "F50E5334-415D-482F-A30D-0623C082B602";

		LocalizedError error = ErrorCode.MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT.format(populationId);
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(error);
	}
}
