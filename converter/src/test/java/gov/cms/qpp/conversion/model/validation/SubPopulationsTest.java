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

	//TODO: move to new test for SubPopulationLabel
//	@Test
//	void testGetKeysContainsExpected() {
//		assertThat(SubPopulations.getKeys())
//				.containsExactly(SubPopulations.DENEXCEP, SubPopulations.DENEX,
//						SubPopulations.DENOM, SubPopulations.NUMER, SubPopulations.IPOP);
//	}

	@Test
	void testGetExclusiveKeysExcludesExpected() {
		String label = SubPopulationLabel.DENEXCEP.name();
		Set<String> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(label));
		assertWithMessage("Excluded key %s should be absent", label)
				.that(keys).doesNotContain(label);
	}

	@Test
	void testGetExclusiveKeysIncludesOthers() {
		Set<String> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulationLabel.DENEX.name()));

 		assertWithMessage("Non excluded keys should be present")
				.that(keys)
				.containsExactly(SubPopulationLabel.DENEXCEP.name(), SubPopulationLabel.DENOM.name(),
					SubPopulationLabel.NUMER.name(), SubPopulationLabel.IPOP.name(), "IPP");
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

		for (String key : SubPopulations.getExclusiveKeys(Sets.newHashSet("IPOP", "IPP"))) {
			assertThat(SubPopulations.getUniqueIdForKey(key, subPopulation))
					.isSameAs(expected.get(key));
		}
	}

}
