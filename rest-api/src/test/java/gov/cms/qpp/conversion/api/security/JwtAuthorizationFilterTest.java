package gov.cms.qpp.conversion.api.security;

import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.truth.Truth;

//Using jUnit 4 for power mock
@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
@PowerMockIgnore("javax.crypto.*")
public class JwtAuthorizationFilterTest {

	private static final String ORG_TYPE = "registry";
	private static AuthenticationManager authenticationManager;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private FilterChain filterChain;

	@BeforeClass
	public static void setUpStatic() {
		authenticationManager = mock(AuthenticationManager.class);
	}

	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = mock(FilterChain.class);
	}

	@Test
	public void testDoFilterInternal() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withName(JwtAuthorizationFilter.DEFAULT_ORG_NAME)
				.withOrgType(ORG_TYPE);

		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		testJwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithInvalidOrgName() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withName("invalid-name")
				.withOrgType(ORG_TYPE);

		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		testJwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithNoOrgId() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withOrgType(ORG_TYPE);

		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		testJwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithNoOrgType() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withName(JwtAuthorizationFilter.DEFAULT_ORG_NAME);

		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		testJwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithNoHeader() throws IOException, ServletException {
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		testJwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDefaultOrgName() {
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager, JwtAuthorizationFilter.DEFAULT_ORG_NAME);
		Truth.assertThat(testJwtAuthFilter.orgName).isEqualTo(JwtAuthorizationFilter.DEFAULT_ORG_NAME);
	}

	@Test
	public void testGivenOrgName() {
		String expected = "some org name";
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager, expected);
		Truth.assertThat(testJwtAuthFilter.orgName).isEqualTo(expected);
	}
}
