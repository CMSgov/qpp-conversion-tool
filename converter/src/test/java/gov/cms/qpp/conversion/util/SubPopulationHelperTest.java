package gov.cms.qpp.conversion.util;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;

import static com.google.common.truth.Truth.assertThat;

public class SubPopulationHelperTest {

	@Test
	void testMapperWithNumeratorLabel() {
		String label = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.NUMER);

		assertThat(label).isEqualTo("performanceMet");
	}

	@Test
	void testMapperWithDenominatorLabel() {
		String label = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENOM);

		assertThat(label).isEqualTo("eligiblePopulation");
	}

	@Test
	void testMapperDenominatorExclusionLabel() {
		String label = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENEX);

		assertThat(label).isEqualTo("eligiblePopulationExclusion");
	}

	@Test
	void testMapperDenominatorExceptionLabel() {
		String label = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.DENEXCEP);

		assertThat(label).isEqualTo("eligiblePopulationException");
	}

	@Test
	void testMapperWithNonExistentLabel() {
		String label = SubPopulationHelper.measureTypeMap.get(SubPopulationLabel.IPOP);

		assertThat(label).isNull();
	}
}
