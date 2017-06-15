package gov.cms.qpp.conversion.decode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

/**
 * Test class for DecodeResult satisfy JaCoCo code coverage
 */
public class DecodeResultTest {

	@Test
	public void decodeResultTest() {
		DecodeResult result = DecodeResult.valueOf("TREE_FINISHED");
		assertThat("Expect Decode Result to be TREE_FINISHED", result.name(), is(DecodeResult.TREE_FINISHED.name()));
	}
	@Test
	public void decodeResultValuesTest() {
		DecodeResult[] results = DecodeResult.values();
		assertThat("Expect Decode Result to be array size 5", results.length, is(5));
	}
}