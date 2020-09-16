package gov.cms.qpp.conversion.model.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;

class ApmEntityIdsTest {

	private static final String APM_ID_THAT_EXISTS = "DogCow";
	private ApmEntityIds apmEntityIds;


	@BeforeEach
	void setUp() {
		apmEntityIds = new ApmEntityIds("test_apm_entity_ids.json");
	}

	@Test
	void testIdExists() {
		assertThat(apmEntityIds.idExists(APM_ID_THAT_EXISTS)).isTrue();
	}

	@Test
	void testIdDoesNotExistDueToCapitalization() {
		assertThat(apmEntityIds.idExists(APM_ID_THAT_EXISTS.toUpperCase(Locale.ENGLISH))).isFalse();
	}

	@Test
	void testIdDoesNotExists() {
		assertThat(apmEntityIds.idExists("PropertyTaxes")).isFalse();
	}
}
