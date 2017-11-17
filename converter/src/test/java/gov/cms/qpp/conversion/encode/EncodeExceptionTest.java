package gov.cms.qpp.conversion.encode;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;
/**
 * Test to provide Circle CI Coverage on EncodeException
 */
class EncodeExceptionTest {
	@Test
	void getTemplateId() throws Exception {
		EncodeException e = new EncodeException("ErrorMessage", "templateId");
		String value = e.getTemplateId();
		assertWithMessage("Expect to get out what I put in")
				.that(value)
				.isEqualTo("templateId");
	}

}