package gov.cms.qpp.conversion.model.validation;

import gov.cms.qpp.CacheBuilder;
import gov.cms.qpp.model.CacheType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;

class ApmEntityIdsTest {

	private static final String APM_ID_THAT_EXISTS = "DogCow";
	private ApmEntityIds apmEntityIds;


	@BeforeEach
	void setUp() {
		apmEntityIds = CacheBuilder.getEntityIds(CacheType.ApmEntityId);
	}

	@Test
	void testIdExists() {
		assertThat(apmEntityIds.cpcIdExists(APM_ID_THAT_EXISTS)).isTrue();
	}

	@Test
	void testIdDoesNotExistDueToCapitalization() {
		assertThat(apmEntityIds.cpcIdExists(APM_ID_THAT_EXISTS.toUpperCase(Locale.ENGLISH))).isFalse();
	}

	@Test
	void testIdDoesNotExists() {
		assertThat(apmEntityIds.cpcIdExists("PropertyTaxes")).isFalse();
	}
}
