package gov.cms.qpp.conversion.decode;

import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Test class for DecodeResult satisfy JaCoCo code coverage
 */
public class DecodeResultTest {

	@Test
	public void decodeResultTest() {
		DecodeResult result = DecodeResult.valueOf("TREE_FINISHED");
		assertWithMessage("Expect Decode Result to be TREE_FINISHED")
				.that(result)
				.isEquivalentAccordingToCompareTo(DecodeResult.TREE_FINISHED);
	}

	@Test
	public void decodeResultValuesTest() {
		DecodeResult[] results = DecodeResult.values();
		assertWithMessage("Expect Decode Result to be array size 5")
				.that(results)
				.hasLength(5);
	}
}