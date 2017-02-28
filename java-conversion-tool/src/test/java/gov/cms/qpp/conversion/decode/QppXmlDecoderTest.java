package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class QppXmlDecoderTest  {
	
	@Test
	public void sillyCoverageTest() throws Exception {
		assertThat("Should be benign", new QppXmlDecoder().internalDecode(null, null), is(nullValue()));
	}
}
