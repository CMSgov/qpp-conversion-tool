package gov.cms.qpp.conversion.api.model;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class TinNpiCombinationTest {

	@Test
	void testMasking() {
		TinNpiCombination expected = new TinNpiCombination("000111222","3334444555");

		assertThat(expected.getMaskedTin()).isEqualTo("*****1222");
	}
}
