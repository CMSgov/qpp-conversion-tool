package gov.cms.qpp.conversion.model.validation;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

class SubPopulationsTest {

	@Test
	void testGetKeysContainsExpected() {
		assertThat(SubPopulations.getKeys())
				.containsExactly(SubPopulations.DENEXCEP, SubPopulations.DENEX,
						SubPopulations.DENOM, SubPopulations.NUMER, SubPopulations.IPOP);
	}

	@Test
	void testGetExclusiveKeysExcludesExpected() {
		Set<String> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulations.DENEXCEP));
		assertWithMessage("Excluded key %s should be absent", SubPopulations.DENEXCEP)
				.that(keys).doesNotContain(SubPopulations.DENEXCEP);
	}

	@Test
	void testGetExclusiveKeysIncludesOthers() {
		Set<String> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulations.DENEX));

 		assertWithMessage("Non excluded keys should be present")
				.that(keys)
				.containsExactly(SubPopulations.DENEXCEP, SubPopulations.DENOM,
						SubPopulations.NUMER, SubPopulations.IPOP);
	}

	@Test
	void testGetUniqueIdForKeyNullKeyThrowsNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () ->
			SubPopulations.getUniqueIdForKey(null, new SubPopulation()));
	}

	@Test
	void testGetUniqueIdForKeyNullSubPopulationThrowsNullPointerException() {
		Assertions.assertThrows(NullPointerException.class, () ->
			SubPopulations.getUniqueIdForKey("not null", null));
	}

	@Test
	void testGetUniqueIdForKeyInvalidKeyThrowsIllegalArgumentException() {
		Assertions.assertThrows(IllegalArgumentException.class, () ->
			SubPopulations.getUniqueIdForKey("invalid key", new SubPopulation()));
	}

	@Test
	void testGetUniqueIdForKeyReturnsExpectedValue() {
		Map<String, String> expected = new HashMap<>();
		for (String key : SubPopulations.getKeys()) {
			expected.put(key, UUID.randomUUID().toString());
		}

		SubPopulation subPopulation = new SubPopulation();
		subPopulation.setDenominatorExceptionsUuid(expected.get(SubPopulations.DENEXCEP));
		subPopulation.setDenominatorExclusionsUuid(expected.get(SubPopulations.DENEX));
		subPopulation.setDenominatorUuid(expected.get(SubPopulations.DENOM));
		subPopulation.setNumeratorUuid(expected.get(SubPopulations.NUMER));

		for (String key : SubPopulations.getExclusiveKeys(Sets.newHashSet("IPOP", "IPP"))) {
			assertThat(SubPopulations.getUniqueIdForKey(key, subPopulation))
					.isSameAs(expected.get(key));
		}
	}

}
