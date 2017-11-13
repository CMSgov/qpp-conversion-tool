package gov.cms.qpp.acceptance.cpc;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

class CpcPlusAcceptanceTest {

	private static final Path BASE = Paths.get("src/test/resources/cpc_plus/");
	private static final Path SUCCESS = BASE.resolve("success");
	private static final Path FAILURE = BASE.resolve("failure");
	private static final Path FAILURE_FIXTURE = FAILURE.resolve("fixture.json");
	private static Map fixtureValues;

	@BeforeAll
	static void setup() throws IOException {
		fixtureValues = JsonHelper.readJson(FAILURE_FIXTURE, Map.class);
	}

	@Test
	void testCpcPlusFileSuccesses() throws IOException {
		Map<Path, AllErrors> errors = new HashMap<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(SUCCESS)) {
			for (Path entry : stream) {
				Files.move(entry, entry.resolveSibling(entry.getFileName().toString().replace("-success.xml", ".xml")));
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
				} catch (TransformException failure) {
					errors.put(entry, failure.getDetails());
				}
			}
		}

		assertThat(errors).isEmpty();
	}

	@Test
	void testCpcPlusFileFailures() throws IOException {
		List<Path> successesThatShouldBeErrors = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(FAILURE)) {
			for (Path entry : stream) {
				if (!entry.toAbsolutePath().endsWith("CPCPlus_CMS122v5IncUUID_SampleQRDA-III.xml")) {
					continue;
				}
				Converter converter = new Converter(new PathQrdaSource(entry));

				try {
					converter.transform();
					successesThatShouldBeErrors.add(entry);
				} catch (TransformException expected) {
					//runnning conversions on individual files
					List<Detail> details = expected.getDetails().getErrors().get(0).getDetails();
					verifyOutcome(entry.toFile().getName(), details);
					System.out.println();
				}
			}
		}

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

	@Test
	void testCpcPlusFilesAreAllChecked() throws IOException {
		long invalidFiles = Files.list(BASE).filter(file -> {
			String fileName = file.toString();

			return fileName.endsWith(".xml");
		}).count();

		assertThat(invalidFiles).isEqualTo(0);
	}
}
