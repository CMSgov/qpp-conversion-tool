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
	void testGetExclusiveKeysExcludesExpected() {
		Set<SubPopulationLabel> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulationLabel.DENEXCEP));
		assertWithMessage("Excluded key %s should be absent", SubPopulationLabel.DENEXCEP)
				.that(keys).doesNotContain(SubPopulationLabel.DENEXCEP);
	}

	@Test
	void testGetExclusiveKeysIncludesOthers() {
		Set<SubPopulationLabel> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulationLabel.DENEX));

 		assertWithMessage("Non excluded keys should be present")
				.that(keys)
				.containsExactly(SubPopulationLabel.DENEXCEP, SubPopulationLabel.DENOM,
					SubPopulationLabel.NUMER, SubPopulationLabel.IPOP);
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
		for (String key : SubPopulationLabel.aliasSet()) {
			expected.put(key, UUID.randomUUID().toString());
		}

		SubPopulation subPopulation = new SubPopulation();
		subPopulation.setDenominatorExceptionsUuid(expected.get(SubPopulationLabel.DENEXCEP.name()));
		subPopulation.setDenominatorExclusionsUuid(expected.get(SubPopulationLabel.DENEX.name()));
		subPopulation.setDenominatorUuid(expected.get(SubPopulationLabel.DENOM.name()));
		subPopulation.setNumeratorUuid(expected.get(SubPopulationLabel.NUMER.name()));

		for (SubPopulationLabel key : SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulationLabel.IPOP))) {
			assertThat(SubPopulations.getUniqueIdForKey(key.name(), subPopulation))
					.isSameInstanceAs(expected.get(key.name()));
		}
	}

}
