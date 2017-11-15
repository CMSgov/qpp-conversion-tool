package gov.cms.qpp.acceptance.cpc;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

	@Test
	void testCpcPlusFileSuccesses() throws IOException {
		Map<Path, AllErrors> errors = new HashMap<>();
		getXml(SUCCESS)
			.forEach(entry -> {
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
				} catch (TransformException failure) {
					errors.put(entry, failure.getDetails());
				}
			});

		assertThat(errors).isEmpty();
	}

	@Test
	void testCpcPlusFileFailures() throws IOException {
		List<Path> successesThatShouldBeErrors = getXml(FAILURE)
			.filter(entry -> {
				Converter converter = new Converter(new PathQrdaSource(entry));
				//TODO remove this once all error scenarios are validated
				if (fixtureValues.get(entry.toFile().getName()) == null) {
					return false;
				}
				try {
					converter.transform();
					return true;
				} catch (TransformException expected) {
					//runnning conversions on individual files
					List<Detail> details = expected.getDetails().getErrors().get(0).getDetails();
					verifyOutcome(entry.toFile().getName(), details);
					return false;
				}
			}).collect(Collectors.toList());

		assertWithMessage("The fixture file is not representative of the failures directory")
				.that(getXml(FAILURE).count())
				.isEqualTo(fixtureValues.size());
		assertThat(successesThatShouldBeErrors).isEmpty();
	}

	private void verifyOutcome(String filename, List<Detail> details) {
		CPCAcceptanceFixture expectedErrors = fixtureValues.get(filename);

		System.out.println("Verifying scenario " + filename +
				", expected errors match the following actual errors. " + details);

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

	private Stream<Path> getXml(Path directory) {
		try {
			return Files.list(directory).filter(this::isXml);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean isXml(Path path) {
		return path.toString().endsWith(".xml");
	}
}
