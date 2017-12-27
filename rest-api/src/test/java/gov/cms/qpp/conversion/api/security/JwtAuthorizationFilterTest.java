package gov.cms.qpp.conversion.api.security;

import gov.cms.qpp.conversion.api.helper.JwtTestHelper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//Using jUnit 4 for power mock
@RunWith(PowerMockRunner.class)
@PrepareForTest({SecurityContextHolder.class})
public class JwtAuthorizationFilterTest {

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
		request.addHeader("Authorization", JwtTestHelper.createJwt());
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		testJwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithWrongToken() throws IOException, ServletException {
		request.addHeader("Authorization", JwtTestHelper.createJwtWithInvalidId());
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
}
