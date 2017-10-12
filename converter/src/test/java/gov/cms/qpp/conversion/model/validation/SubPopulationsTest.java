package gov.cms.qpp.conversion.model.validation;

import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class SubPopulationsTest {

	@Test
	public void testGetKeysContainsExpected() {
		assertThat(SubPopulations.getKeys())
				.containsExactly(SubPopulations.DENEXCEP, SubPopulations.DENEX,
						SubPopulations.DENOM, SubPopulations.NUMER, SubPopulations.IPOP);
	}

	@Test
	public void testGetExclusiveKeysExcludesExpected() {
		Set<String> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulations.DENEXCEP));
		assertWithMessage("Excluded key %s should be absent", SubPopulations.DENEXCEP)
				.that(keys).doesNotContain(SubPopulations.DENEXCEP);
	}

	@Test
	public void testGetExclusiveKeysIncludesOthers() {
		Set<String> keys = SubPopulations.getExclusiveKeys(Sets.newHashSet(SubPopulations.DENEX));

 		assertWithMessage("Non excluded keys should be present")
				.that(keys)
				.containsExactly(SubPopulations.DENEXCEP, SubPopulations.DENOM,
						SubPopulations.NUMER, SubPopulations.IPOP);
	}

	@Test(expected = NullPointerException.class)
	public void testGetUniqueIdForKeyNullKeyThrowsNullPointerException() {
		SubPopulations.getUniqueIdForKey(null, new SubPopulation());
	}

	@Test(expected = NullPointerException.class)
	public void testGetUniqueIdForKeyNullSubPopulationThrowsNullPointerException() {
		SubPopulations.getUniqueIdForKey("not null", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUniqueIdForKeyInvalidKeyThrowsIllegalArgumentException() {
		SubPopulations.getUniqueIdForKey("invalid key", new SubPopulation());
	}

	@Test
	public void testGetUniqueIdForKeyReturnsExpectedValue() {
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
