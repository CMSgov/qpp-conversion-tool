package gov.cms.qpp.conversion.model.validation;

import org.junit.Test;

import static junit.framework.Assert.assertNull;

/**
 * SubPopulation Test class to increase JaCoCo Code Coverage
 */
public class SubPopulationTest {
	@Test
	public void getStrata1() throws Exception {
		SubPopulation sp = new SubPopulation();
		String strata1 = sp.getStrata1();
		assertNull(strata1);
	}

	@Test
	public void getStrata2() throws Exception {
		SubPopulation sp = new SubPopulation();
		String strata2 = sp.getStrata2();
		assertNull(strata2);
	}
}