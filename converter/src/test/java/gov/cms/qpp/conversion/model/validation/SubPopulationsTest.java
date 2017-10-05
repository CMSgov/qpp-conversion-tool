package gov.cms.qpp.conversion.model.validation;

import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubPopulationsTest {

	@Test
	public void testGetKeysContainsExpected() {
		Assert.assertThat(SubPopulations.getKeys(),
				Matchers.containsInAnyOrder(SubPopulations.DENEXCEP, SubPopulations.DENEX,
						SubPopulations.DENOM, SubPopulations.NUMER, SubPopulations.IPOP);
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
			Assert.assertEquals(expected.get(key), SubPopulations.getUniqueIdForKey(key, subPopulation));
		}
	}

}
