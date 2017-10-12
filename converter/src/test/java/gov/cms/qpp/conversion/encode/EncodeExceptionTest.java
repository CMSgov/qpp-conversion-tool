package gov.cms.qpp.conversion.encode;

import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;
/**
 * Test to provide Circle CI Coverage on EncodeException
 */
public class EncodeExceptionTest {
	@Test
	public void getTemplateId() throws Exception {
		EncodeException e = new EncodeException("ErrorMessage", "templateId");
		String value = e.getTemplateId();
		assertWithMessage("Expect to get out what I put in")
				.that(value)
				.isEqualTo("templateId");
	}

}