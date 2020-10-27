package gov.cms.qpp.acceptance.cpc;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.util.JsonHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class CpcPlusAcceptanceTest {

	private static final Path BASE = Paths.get("src/test/resources/cpc_plus/");
	private static final Path SUCCESS = BASE.resolve("success");
	private static final Path SUCCESS_2020 = BASE.resolve("success/2020");
	private static final Path SUCCESS_2020_WARNINGS = BASE.resolve("success/2020/warnings");
	private static final Path FAILURE = BASE.resolve("failure");
	private static final Path FAILURE_2020 = BASE.resolve("failure/2020");
	private static final Path FAILURE_FIXTURE = FAILURE.resolve("fixture.json");
	private static Map<String, CPCAcceptanceFixture> fixtureValues;
	private ApmEntityIds apmEntityIds = new ApmEntityIds("test_apm_entity_ids.json");

	@BeforeAll
	static void setUp() throws IOException {
		TypeReference<Map<String, CPCAcceptanceFixture>> ref =
				new TypeReference<Map<String, CPCAcceptanceFixture>>() { };
		fixtureValues = JsonHelper.readJson(FAILURE_FIXTURE, ref);
	}

	@BeforeEach
	void measureConfigSetup() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
	}

	@AfterEach
	void measureConfigTeardown() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	static Stream<Path> successData() {
		return getXml(SUCCESS);
	}

	static Stream<Path> failureData() {
		return getXml(FAILURE);
	}

	static Stream<Path> success2020Data() {
		return getXml(SUCCESS_2020);
	}

	static Stream<Path> success2020DataWithWarnings() {
		return getXml(SUCCESS_2020_WARNINGS);
	}

	static Stream<Path> failure2020Data() {
		return getXml(FAILURE_2020);
	}

	private static Stream<Path> getXml(Path directory) {
		try {
			return Files.list(directory).filter(CpcPlusAcceptanceTest::isXml);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static boolean isXml(Path path) {
		return path.toString().endsWith(".xml");
	}

	@ParameterizedTest
	@MethodSource("successData")
	void testCpcPlusFileSuccesses(Path entry) {
		AllErrors errors = null;
		List<Detail> warnings = null;

		Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

		try {
			converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
			warnings = failure.getConversionReport().getWarnings();
		}

		assertThat(errors).isNull();
		assertThat(warnings).isNull();
	}

	@ParameterizedTest
	@MethodSource("failureData")
	void testCpcPlusFileFailures(Path entry) {
		String fileName = entry.getFileName().toString();
		assertWithMessage("No associated entry in fixture.json for the file %s", fileName).that(fixtureValues).containsKey(fileName);

		Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

		TransformException expected = Assertions.assertThrows(TransformException.class, converter::transform);
		//running conversions on individual files
		List<Detail> details = expected.getDetails().getErrors().get(0).getDetails();
		verifyOutcome(fileName, details);
	}

	@ParameterizedTest
	@MethodSource("success2020Data")
	void test2020CpcPlusValidFiles(Path entry) {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		AllErrors errors = null;
		List<Detail> warnings = null;

		Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

		try {
			converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
			warnings = failure.getConversionReport().getWarnings();
		}

		assertThat(errors).isNull();
		assertThat(warnings).isNull();
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@ParameterizedTest
	@MethodSource("success2020DataWithWarnings")
	void test2020CpcPlusWarningFiles(Path entry) {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		AllErrors errors = null;

		Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

		try {
			converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
		}

		assertThat(errors).isNull();
		assertThat(converter.getReport().getWarnings()).isNotNull();
		assertThat(converter.getReport().getWarnings()).isNotEmpty();
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@ParameterizedTest
	@MethodSource("failure2020Data")
	void test2020CpcPlusInvalidFiles(Path entry) {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		String fileName = entry.getFileName().toString();
		assertWithMessage("No associated entry in fixture.json for the file %s", fileName).that(fixtureValues).containsKey(fileName);

		Converter converter = new Converter(new PathSource(entry), new Context(apmEntityIds));

		TransformException expected = Assertions.assertThrows(TransformException.class, converter::transform);
		//running conversions on individual files
		List<Detail> details = expected.getDetails().getErrors().get(0).getDetails();
		verifyOutcome(fileName, details);
	}

	private void verifyOutcome(String filename, List<Detail> details) {
		CPCAcceptanceFixture expectedErrors = fixtureValues.get(filename);

		if (expectedErrors.isStrict()) {
			int totalErrors = expectedErrors.getErrorData().stream()
					.mapToInt(FixtureErrorData::getOccurrences)
					.sum();

			assertWithMessage("Error count mismatch for %s", filename)
					.that(details).hasSize(totalErrors);
		}

		expectedErrors.getErrorData().forEach(expectedError -> {
			Integer expectedErrorCode = expectedError.getErrorCode();
			String expectedErrorMessage = expectedError.getMessage();

			long matchingActualErrors = details.stream()
				.filter(actualError -> actualError.getErrorCode().equals(expectedErrorCode))
				.filter(actualError -> messageComparison(actualError.getMessage(), expectedErrorMessage))
				.count();

			assertWithMessage("The actual number of occurrences for the error code %s and substitutions %s did not match",
					expectedError.getErrorCode(),
					expectedError.getSubs())
				.that(matchingActualErrors).isEqualTo(expectedError.getOccurrences());
		});
	}

	private boolean messageComparison(String actual, String expected) {
		return actual.equals(expected) ||
				actual.replaceAll("[\\[()\\]\\+]", "")
						.matches(expected.replaceAll("[()+]", ""));
	}
}
