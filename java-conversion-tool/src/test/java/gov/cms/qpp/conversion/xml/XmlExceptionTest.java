package gov.cms.qpp.conversion.xml;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test class to provide JaCoCo code coverage
 */
public class XmlExceptionTest {

	@Test
	public void xmlExceptionTest() {
		String reason = "because I said so";
		XmlException e = new XmlException(reason);
		assertThat("Expect the message to be the same", e.getMessage(), is(reason));
	}

	@Test
	public void xmlExceptionFromExceptionTest() {
		String reason = "/ by zero";
		int y = 0;
		int x = 1;
		XmlException xmlException = null;
		try {
			int z = x/y;
		}
		catch(ArithmeticException e){
			xmlException = new XmlException(reason, e);
		}
		assertThat("Expect to have a Division By Zero Exception",
			xmlException.getCause().getMessage(), is(reason));
	}
}