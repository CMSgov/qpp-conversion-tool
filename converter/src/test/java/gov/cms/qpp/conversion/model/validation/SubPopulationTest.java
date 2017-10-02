package gov.cms.qpp.conversion.model.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * SubPopulation Test class to increase JaCoCo Code Coverage
 */
public class SubPopulationTest {

	@Test
	public void getStrata() {
		SubPopulation sp = new SubPopulation();
		List<String> strata = sp.getStrata();
		assertTrue(strata.isEmpty());
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(SubPopulation.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

}