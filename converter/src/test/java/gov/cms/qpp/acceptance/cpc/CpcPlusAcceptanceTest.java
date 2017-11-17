package gov.cms.qpp.acceptance.cpc;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;


class CpcPlusAcceptanceTest {

	private static final Path BASE = Paths.get("src/test/resources/cpc_plus/");
	private static final Path SUCCESS = BASE.resolve("success");
	private static final Path FAILURE = BASE.resolve("failure");

	@BeforeAll
	static void initMockApmIds() {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
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

				try {
					converter.transform();
					return true;
				} catch (TransformException expected) {
					return false;
				}
			}).collect(Collectors.toList());

		assertThat(successesThatShouldBeErrors).isEmpty();
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
