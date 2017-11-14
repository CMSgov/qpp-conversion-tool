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
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;


class CpcPlusAcceptanceTest {

	private static final Path BASE = Paths.get("src/test/resources/cpc_plus/");
	private static final Path SUCCESS = BASE.resolve("success");
	private static final Path FAILURE = BASE.resolve("failure");
	private static final Path FAILURE_FIXTURE = FAILURE.resolve("fixture.json");
	private static Map<String, List<CPCAcceptanceFixture>> fixtureValues;

	@BeforeAll
	static void initMockApmIds() throws IOException {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
		TypeReference<Map<String, List<CPCAcceptanceFixture>>> ref =
				new TypeReference<Map<String, List<CPCAcceptanceFixture>>>() { };
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
					System.out.println();
					return false;
				}
			}).collect(Collectors.toList());

		assertThat(successesThatShouldBeErrors).isEmpty();
	}

	private void verifyOutcome(String filename, List<Detail> details) {
		List<CPCAcceptanceFixture> expectedErrors = fixtureValues.get(filename);
		Map<String, CPCAcceptanceFixture> errorMap =
				expectedErrors.stream().collect(
						Collectors.toMap(CPCAcceptanceFixture::getMessage, Function.identity()));

		details.forEach(detail -> {
			String message = detail.getMessage();
			CPCAcceptanceFixture fixture = errorMap.get(message);
			assertThat(fixture).isNotNull();
			assertThat(detail.getErrorCode()).isEqualTo(fixture.getErrorCode());

			fixture.decrementOccurrances();
			if (fixture.getOccurrences() <= 0) {
				errorMap.remove(message);
			}
		});

		assertThat(errorMap).hasSize(0);
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
