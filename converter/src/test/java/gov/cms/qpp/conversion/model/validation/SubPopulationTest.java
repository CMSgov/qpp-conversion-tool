package gov.cms.qpp.conversion.model.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static org.junit.Assert.assertNull;

/**
 * SubPopulation Test class to increase JaCoCo Code Coverage
 */
public class SubPopulationTest {

	@Test
	public void getStrata1() {
		SubPopulation sp = new SubPopulation();
		String strata1 = sp.getStrata1();
		assertNull(strata1);
	}

	@Test
	public void getStrata2() {
		SubPopulation sp = new SubPopulation();
		String strata2 = sp.getStrata2();
		assertNull(strata2);
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(SubPopulation.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

}