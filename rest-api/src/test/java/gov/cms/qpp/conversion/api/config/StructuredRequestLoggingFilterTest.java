package gov.cms.qpp.conversion.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StructuredRequestLoggingFilterTest {

	private final StructuredRequestLoggingFilter filter = new StructuredRequestLoggingFilter();

	@AfterEach
	void cleanup() {
		MDC.clear();
	}

	@Test
	void logsAndClearsMdcForNonMultipartRequest() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);

		when(request.getMethod()).thenReturn("GET");
		when(request.getRequestURI()).thenReturn("/api/v1/convert");
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getHeader("User-Agent")).thenReturn("junit");
		when(request.getProtocol()).thenReturn("HTTP/1.1");
		when(request.getContentType()).thenReturn("application/json");
		when(response.getStatus()).thenReturn(200);

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
		assertThat(MDC.get("requestStage")).isNull();
		assertThat(MDC.get("httpMethod")).isNull();
	}

	@Test
	void capturesAttachmentHashForMultipartRequest() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		jakarta.servlet.http.Part part = mock(jakarta.servlet.http.Part.class);

		when(request.getMethod()).thenReturn("POST");
		when(request.getRequestURI()).thenReturn("/api/v1/convert");
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getProtocol()).thenReturn("HTTP/1.1");
		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getParts()).thenReturn(Collections.singletonList(part));
		when(response.getStatus()).thenReturn(201);

		filter.doFilter(request, response, chain);

		verify(chain).doFilter(request, response);
		assertThat(MDC.get("attachment")).isNull();
	}
}
