package gov.cms.qpp.conversion.model.validation;

import gov.cms.qpp.conversion.util.JsonReadException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApmEntityIdsTest {

	private static final String APM_ID_THAT_EXISTS = "DogCow";

	@BeforeAll
	static void setUp() {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
	}

	@AfterAll
	static void tearDown() {
		ApmEntityIds.setApmDataFile(ApmEntityIds.DEFAULT_APM_ENTITY_FILE_NAME);
	}

	@Test
	void testIdExists() {
		assertThat(ApmEntityIds.idExists(APM_ID_THAT_EXISTS)).isTrue();
	}

	@Test
	void testIdDoesNotExistDueToCapitalization() {
		assertThat(ApmEntityIds.idExists(APM_ID_THAT_EXISTS.toUpperCase(Locale.ENGLISH))).isFalse();
	}

	@Test
	void testIdDoesNotExists() {
		assertThat(ApmEntityIds.idExists("PropertyTaxes")).isFalse();
	}

	@Test
	void testNonExistentFileNotLooseData() {
		assertThrows(JsonReadException.class, () -> ApmEntityIds.setApmDataFile("file_does_not_exist.json"));
		assertThat(ApmEntityIds.idExists(APM_ID_THAT_EXISTS)).isTrue();
	}
}
