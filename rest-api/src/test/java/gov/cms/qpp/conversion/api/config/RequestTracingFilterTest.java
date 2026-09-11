package gov.cms.qpp.conversion.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestTracingFilterTest {

	private final RequestTracingFilter filter = new RequestTracingFilter();

	@AfterEach
	void cleanup() {
		MDC.clear();
	}

	@Test
	void generatesRequestIdWhenHeaderAbsent() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		when(request.getHeader(RequestTracingFilter.REQUEST_ID_HEADER)).thenReturn(null);

		filter.doFilter(request, response, chain);

		verify(response).setHeader(anyString(), anyString());
		verify(chain).doFilter(request, response);
		assertThat(MDC.get(RequestTracingFilter.REQUEST_ID_MDC_KEY)).isNull();
	}

	@Test
	void reusesExistingRequestIdHeader() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		String existingId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
		when(request.getHeader(RequestTracingFilter.REQUEST_ID_HEADER)).thenReturn(existingId);

		filter.doFilter(request, response, chain);

		verify(response).setHeader(RequestTracingFilter.REQUEST_ID_HEADER, existingId);
		verify(chain).doFilter(request, response);
	}

	@Test
	void generatesNewRequestIdWhenHeaderIsMalformed() throws Exception {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		String malformedId = "<script>alert(1)</script>";
		when(request.getHeader(RequestTracingFilter.REQUEST_ID_HEADER)).thenReturn(malformedId);

		filter.doFilter(request, response, chain);

		verify(response, org.mockito.Mockito.never()).setHeader(RequestTracingFilter.REQUEST_ID_HEADER, malformedId);
		verify(response).setHeader(anyString(), anyString());
		verify(chain).doFilter(request, response);
	}
}
