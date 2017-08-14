package gov.cms.qpp.conversion.api.helper;

import org.junit.Before;
import org.junit.Test;
import ch.qos.logback.classic.spi.LoggingEvent;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;

public class ThreadRequestPartConverterTest {
	ThreadRequestPartConverter converter;
	LoggingEvent event = new LoggingEvent();

	@Before
	public void setup() {
		converter = spy(new ThreadRequestPartConverter());
	}

	@Test
	public void convertPartlessTest() throws IOException, ServletException {
		doReturn(null).when(converter).getPart();
		String result = converter.convert(event);

		assertThat("result should equal event thread name", result, is(event.getThreadName()));
	}

	@Test
	public void convertPartfulTest() throws IOException, ServletException {
		Part part = mock(Part.class);
		doReturn(part).when(converter).getPart();
		String result = converter.convert(event);

		assertThat("should contain part's hash code", result,
				allOf(containsString(event.getThreadName()), containsString(String.valueOf(part.hashCode()))));
	}

	@Test
	public void convertIOExceptionOnPartRetrieval() throws IOException, ServletException {
		doThrow(new IOException()).when(converter).getPart();
		String result = converter.convert(event);

		assertThat("IOException - result should equal event thread name",
				result, is(event.getThreadName()));
	}

	@Test
	public void convertServletExceptionOnPartRetrieval() throws IOException, ServletException {
		doThrow(new ServletException()).when(converter).getPart();
		String result = converter.convert(event);

		assertThat("ServletException - result should equal event thread name",
				result, is(event.getThreadName()));
	}

}
