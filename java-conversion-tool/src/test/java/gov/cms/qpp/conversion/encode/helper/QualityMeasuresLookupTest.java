package gov.cms.qpp.conversion.encode.helper;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Class to test the Quality Measures Lookup helper
 */
public class QualityMeasuresLookupTest {
	@Test
	public void getMeasureIdExists() throws Exception {
		assertThat("Measure id exists in lookup",
				QualityMeasuresLookup.getMeasureId("40280381-51f0-825b-0152-22a1e7e81737"), is("CMS130v5"));
	}

	@Test
	public void getMeasureIdNotExists() throws Exception {
		String invalidValue = "invalidValue";
		assertThat("Measure id does not exists in lookup",
				QualityMeasuresLookup.getMeasureId(invalidValue), is(invalidValue));
	}
}