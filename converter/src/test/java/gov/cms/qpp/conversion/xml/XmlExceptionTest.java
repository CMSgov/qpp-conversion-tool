package gov.cms.qpp.conversion.xml;

import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Test class to provide JaCoCo code coverage
 */
public class XmlExceptionTest {

	@Test
	public void xmlExceptionTest() {
		String reason = "because I said so";
		XmlException e = new XmlException(reason);
		assertWithMessage("Expect the message to be the same")
				.that(e.getMessage()).isSameAs(reason);
	}

	@Test
	public void xmlExceptionFromExceptionTest() {
		String reason = "a reason";
		XmlException xmlException = new XmlException("meep", new Exception(reason));
		assertWithMessage("Expected a different reason")
				.that(xmlException)
				.hasCauseThat()
				.hasMessageThat()
				.isSameAs(reason);
	}
}