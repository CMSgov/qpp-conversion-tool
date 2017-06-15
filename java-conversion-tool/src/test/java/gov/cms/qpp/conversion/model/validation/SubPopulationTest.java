package gov.cms.qpp.conversion.model.validation;

import static org.junit.Assert.assertNull;

import org.junit.Test;

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
}