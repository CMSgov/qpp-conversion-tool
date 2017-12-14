package gov.cms.qpp.conversion.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import gov.cms.qpp.conversion.logging.AttachmentHashPartConverter;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

public class AttachmentHashPartConverterTest {
	private AttachmentHashPartConverter converter;
	private LoggingEvent event = new LoggingEvent();

	@Before
	public void setup() {
		converter = spy(new AttachmentHashPartConverter());
	}

	@Test
	public void convertPartlessTest() throws IOException, ServletException {
		doReturn(null).when(converter).getPart();
		String result = converter.convert(event);

		assertWithMessage("result should equal empty string")
				.that(result).isEqualTo("");
	}

	@Test
	public void convertPartfulTest() throws IOException, ServletException {
		Part part = mock(Part.class);
		doReturn(part).when(converter).getPart();
		String result = converter.convert(event);

		assertWithMessage("should equal part's hash code")
				.that(result).isEqualTo(String.valueOf(part.hashCode()));
	}

	@Test
	public void convertIOExceptionOnPartRetrieval() throws IOException, ServletException {
		doThrow(new IOException()).when(converter).getPart();
		String result = converter.convert(event);

		assertWithMessage("IOException - result should equal empty string")
				.that(result).isEqualTo("");
	}

	@Test
	public void convertServletExceptionOnPartRetrieval() throws IOException, ServletException {
		doThrow(new ServletException()).when(converter).getPart();
		String result = converter.convert(event);

		assertWithMessage("ServletException - result should equal an empty sting")
				.that(result).isEqualTo("");
	}

}
