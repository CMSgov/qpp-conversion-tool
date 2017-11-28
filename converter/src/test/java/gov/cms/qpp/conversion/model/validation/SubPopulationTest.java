package gov.cms.qpp.conversion.model.validation;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * SubPopulation Test class to increase JaCoCo Code Coverage
 */
class SubPopulationTest {

	@Test
	void getStrata() {
		SubPopulation sp = new SubPopulation();
		List<String> strata = sp.getStrata();

		assertThat(strata).isEmpty();
	}

	@Test
	void copyConstructor() {
		SubPopulation sp = new SubPopulation();
		SubPopulation otherSp = new SubPopulation(sp);
		
		assertThat(sp).isEqualTo(otherSp);
	}

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(SubPopulation.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

}