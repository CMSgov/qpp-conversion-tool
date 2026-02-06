package gov.cms.qpp.conversion.api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAuthorizationFilterTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private ServletResponse response;

	@Mock
	private FilterChain chain;

	@AfterEach
	void cleanupSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void doFilter_validTokenAndOrg_setsAuthentication_andContinuesChain() throws Exception {
		JwtAuthorizationFilter filter = new JwtAuthorizationFilter();

		String jwt = jwtWithData(Map.of(
				"id", "user-123",
				"name", JwtAuthorizationFilter.DEFAULT_ORG_NAME,   // "cpc-test"
				"orgType", "CPC"
		));

		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

		filter.doFilter(request, response, chain);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		assertEquals("user-123", auth.getPrincipal());
		verify(chain).doFilter(request, response);
	}

	@Test
	void doFilter_invalidOrg_doesNotSetAuthentication_andContinuesChain() throws Exception {
		JwtAuthorizationFilter filter = new JwtAuthorizationFilter();

		String jwt = jwtWithData(Map.of(
				"id", "user-123",
				"name", "not-allowed-org",
				"orgType", "CPC"
		));

		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);

		filter.doFilter(request, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(chain).doFilter(request, response);
	}

	@Test
	void doFilter_missingHeader_doesNotSetAuthentication_andContinuesChain() throws Exception {
		JwtAuthorizationFilter filter = new JwtAuthorizationFilter();

		when(request.getHeader("Authorization")).thenReturn(null);

		filter.doFilter(request, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(chain).doFilter(request, response);
	}

	@Test
	void doFilter_nonHttpRequest_doesNotSetAuthentication_andContinuesChain() throws Exception {
		JwtAuthorizationFilter filter = new JwtAuthorizationFilter();

		ServletRequest nonHttp = org.mockito.Mockito.mock(ServletRequest.class);

		filter.doFilter(nonHttp, response, chain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(chain).doFilter(nonHttp, response);
	}

	/**
	 * Builds a compact JWT string "header.payload.signature" where header/payload are base64url JSON.
	 * Your filter strips the signature and parses it using parseClaimsJwt("header.payload.").
	 */
	private static String jwtWithData(Map<String, String> data) {
		String headerJson = "{\"alg\":\"none\",\"typ\":\"JWT\"}";

		// payload contains: { "data": { ... } }
		StringBuilder dataJson = new StringBuilder();
		dataJson.append("{\"data\":{");
		boolean first = true;
		for (Map.Entry<String, String> e : data.entrySet()) {
			if (!first) dataJson.append(",");
			first = false;
			dataJson.append("\"").append(escape(e.getKey())).append("\":")
					.append("\"").append(escape(e.getValue())).append("\"");
		}
		dataJson.append("}}");

		String header = base64Url(headerJson);
		String payload = base64Url(dataJson.toString());

		// signature can be anything; filter removes it
		return header + "." + payload + ".sig";
	}

	private static String base64Url(String s) {
		return java.util.Base64.getUrlEncoder()
				.withoutPadding()
				.encodeToString(s.getBytes(StandardCharsets.UTF_8));
	}

	private static String escape(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
