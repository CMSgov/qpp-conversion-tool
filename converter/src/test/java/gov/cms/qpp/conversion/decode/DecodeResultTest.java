package gov.cms.qpp.conversion.decode;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Test class for DecodeResult satisfy JaCoCo code coverage
 */
class DecodeResultTest {

	@Test
	void decodeResultTest() {
		DecodeResult result = DecodeResult.valueOf("TREE_FINISHED");
		assertThat(result).isEquivalentAccordingToCompareTo(DecodeResult.TREE_FINISHED);
	}

	@Test
	void decodeResultValuesTest() {
		DecodeResult[] results = DecodeResult.values();
		assertThat(results).hasLength(5);
	}
}