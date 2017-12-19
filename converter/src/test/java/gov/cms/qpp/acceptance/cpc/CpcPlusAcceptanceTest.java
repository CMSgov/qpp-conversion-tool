package gov.cms.qpp.acceptance.cpc;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
	private static final Path FAILURE = BASE.resolve("failure");
	private static final Path FAILURE_FIXTURE = FAILURE.resolve("fixture.json");
	private static Map<String, CPCAcceptanceFixture> fixtureValues;

	@BeforeAll
	static void initMockApmIds() throws IOException {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
		TypeReference<Map<String, CPCAcceptanceFixture>> ref =
				new TypeReference<Map<String, CPCAcceptanceFixture>>() { };
		fixtureValues = JsonHelper.readJson(FAILURE_FIXTURE, ref);
	}

	@AfterAll
	static void resetApmIds() {
		ApmEntityIds.setApmDataFile(ApmEntityIds.DEFAULT_APM_ENTITY_FILE_NAME);
	}

	static Stream<Path> successData() {
		return getXml(SUCCESS);
	}

	static Stream<Path> failureData() {
		return getXml(FAILURE);
	}

	static Stream<Path> getXml(Path directory) {
		try {
			return Files.list(directory).filter(CpcPlusAcceptanceTest::isXml);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	static boolean isXml(Path path) {
		return path.toString().endsWith(".xml");
	}

	@ParameterizedTest
	@MethodSource("successData")
	void testCpcPlusFileSuccesses(Path entry) throws IOException {
		AllErrors errors = null;

		Converter converter = new Converter(new PathSource(entry));

		try {
			converter.transform();
		} catch (TransformException failure) {
			errors = failure.getDetails();
		}

		assertThat(errors).isNull();
	}

	@ParameterizedTest
	@MethodSource("failureData")
	void testCpcPlusFileFailures(Path entry) throws IOException {
		String fileName = entry.getFileName().toString();
		assertWithMessage("No associated entry in fixture.json for the file %s", fileName).that(fixtureValues).containsKey(fileName);

		Converter converter = new Converter(new PathSource(entry));

		TransformException expected = Assertions.assertThrows(TransformException.class, converter::transform);
		//runnning conversions on individual files
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

		expectedErrors.getErrorData().stream().forEach(expectedError -> {
			Integer expectedErrorCode = expectedError.getErrorCode();
			String expectedErrorMessage = expectedError.getMessage();

			long matchingActualErrors = details.stream()
				.filter(actualError -> actualError.getErrorCode() == expectedErrorCode)
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
				actual.replaceAll("[\\[()\\]]", "")
						.matches(expected.replaceAll("[()]", ""));
	}
}
