package gov.cms.qpp.conversion.model.validation;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * SubPopulation Test class to increase JaCoCo Code Coverage
 */
public class SubPopulationTest {

	@Test
	public void getStrata() {
		SubPopulation sp = new SubPopulation();
		List<String> strata = sp.getStrata();

		assertThat(strata).isEmpty();
	}

	@Test
	public void copyConstructor() {
		SubPopulation sp = new SubPopulation();
		SubPopulation otherSp = new SubPopulation(sp);
		
		assertThat(sp).isEqualTo(otherSp);
	}

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(SubPopulation.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

}