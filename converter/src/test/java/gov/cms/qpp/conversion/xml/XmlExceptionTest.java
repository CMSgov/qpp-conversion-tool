package gov.cms.qpp.conversion.xml;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.Test;

/**
 * Test class to provide JaCoCo code coverage
 */
class XmlExceptionTest {

	@Test
	void xmlExceptionTest() {
		String reason = "because I said so";
		XmlException e = new XmlException(reason);
		assertWithMessage("Expect the message to be the same")
				.that(e.getMessage()).isSameAs(reason);
	}

	@Test
	void xmlExceptionFromExceptionTest() {
		String reason = "a reason";
		XmlException xmlException = new XmlException("meep", new Exception(reason));
		assertWithMessage("Expected a different reason")
				.that(xmlException)
				.hasCauseThat()
				.hasMessageThat()
				.isSameAs(reason);
	}
}