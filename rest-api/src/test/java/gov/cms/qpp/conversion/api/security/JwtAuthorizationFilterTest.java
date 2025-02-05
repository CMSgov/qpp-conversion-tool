package gov.cms.qpp.conversion.api.security;

import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//Using jUnit 4 for power mock
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SecurityContextHolder.class})
@PowerMockIgnore({"org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*", "com.sun.org.apache.xerces.*", "javax.crypto.*"})

public class JwtAuthorizationFilterTest {

	private static final String ORG_TYPE = "registry";

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private FilterChain filterChain;

//	@Before
//	public void setUp() {
//		request = new MockHttpServletRequest();
//		response = new MockHttpServletResponse();
//		filterChain = mock(FilterChain.class);
//	}
//
//	@Test
//	public void testdoFilter() throws IOException, ServletException {
//		JwtPayloadHelper payload = new JwtPayloadHelper()
//				.withName(JwtAuthorizationFilter.DEFAULT_ORG_NAME)
//				.withOrgType(ORG_TYPE);
//
//		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter();
//
//		PowerMockito.mockStatic(SecurityContextHolder.class);
//		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);
//
//		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
//
//		testJwtAuthFilter.doFilter(request, response, filterChain);
//
//		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
//		verify(SecurityContextHolder.getContext(), times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
//	}
//
//	@Test
//	public void testdoFilterWithInvalidOrgName() throws IOException, ServletException {
//		JwtPayloadHelper payload = new JwtPayloadHelper()
//				.withName("invalid-name")
//				.withOrgType(ORG_TYPE);
//
//		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter();
//
//		PowerMockito.mockStatic(SecurityContextHolder.class);
//		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);
//
//		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
//
//		testJwtAuthFilter.doFilter(request, response, filterChain);
//
//		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
//		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
//	}
//
//	@Test
//	public void testdoFilterWithNoOrgId() throws IOException, ServletException {
//		JwtPayloadHelper payload = new JwtPayloadHelper()
//				.withOrgType(ORG_TYPE);
//
//		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter();
//
//		PowerMockito.mockStatic(SecurityContextHolder.class);
//		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);
//
//		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
//
//		testJwtAuthFilter.doFilter(request, response, filterChain);
//
//		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
//		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
//	}
//
//	@Test
//	public void testdoFilterWithNoOrgType() throws IOException, ServletException {
//		JwtPayloadHelper payload = new JwtPayloadHelper()
//				.withName(JwtAuthorizationFilter.DEFAULT_ORG_NAME);
//
//		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter();
//
//		PowerMockito.mockStatic(SecurityContextHolder.class);
//		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);
//
//		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
//
//		testJwtAuthFilter.doFilter(request, response, filterChain);
//
//		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
//		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
//	}
//
//	@Test
//	public void testdoFilterWithNoHeader() throws IOException, ServletException {
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter();
//
//		PowerMockito.mockStatic(SecurityContextHolder.class);
//		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);
//
//		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
//
//		testJwtAuthFilter.doFilter(request, response, filterChain);
//
//		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
//		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
//	}
//
//	@Test
//	public void testDefaultOrgName() {
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(JwtAuthorizationFilter.DEFAULT_ORG_SET);
//		Truth.assertThat(testJwtAuthFilter.orgName).contains(JwtAuthorizationFilter.DEFAULT_ORG_NAME);
//	}
//
//	@Test
//	public void testGivenOrgName() {
//		Set<String> expected = Set.of("some org name");
//		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(expected);
//		Truth.assertThat(testJwtAuthFilter.orgName).isEqualTo(expected);
//	}
}
