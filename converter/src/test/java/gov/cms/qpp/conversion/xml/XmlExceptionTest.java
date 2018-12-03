package gov.cms.qpp.conversion.xml;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.Test;

/**
 * Test class to provide JaCoCo code coverage
 */
class XmlExceptionTest {

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