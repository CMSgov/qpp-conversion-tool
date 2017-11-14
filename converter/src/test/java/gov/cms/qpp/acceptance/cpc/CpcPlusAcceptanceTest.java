package gov.cms.qpp.acceptance.cpc;

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
	private static Map fixtureValues;

	@BeforeAll
	static void initMockApmIds() throws IOException {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
		fixtureValues = JsonHelper.readJson(FAILURE_FIXTURE, Map.class);
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
//<<<<<<< HEAD
//		List<Path> successesThatShouldBeErrors = new ArrayList<>();
//		try (DirectoryStream<Path> stream = Files.newDirectoryStream(FAILURE)) {
//			for (Path entry : stream) {
//				if (!entry.toAbsolutePath().endsWith("CPCPlus_CMS122v5IncUUID_SampleQRDA-III.xml")) {
//					continue;
//				}
//=======
		List<Path> successesThatShouldBeErrors = getXml(FAILURE)
			.filter(entry -> {
				Converter converter = new Converter(new PathQrdaSource(entry));

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

		List<Map<String, Object>> expectedErrors = (List<Map<String, Object>>) fixtureValues.get(filename);
		assertThat(details).hasSize(expectedErrors.size());

		Map<String, Detail> detailMap = details.stream()
				.collect(Collectors.toMap(Detail::getPath, Function.identity()));
		expectedErrors.forEach(expectedError -> {
			Detail deet = detailMap.get(expectedError.get("path"));
			assertThat(deet).isNotNull();
			assertThat(deet.getMessage()).isEqualTo(expectedError.get("message"));
			assertThat(deet.getErrorCode()).isEqualTo(expectedError.get("errorCode"));
		});
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
