package gov.cms.qpp.conversion.api.security;

import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;
import gov.cms.qpp.conversion.api.model.Constants;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
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
import static org.mockito.Mockito.when;

//Using jUnit 4 for power mock
@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextHolder.class)
public class JwtAuthorizationFilterTest {

	@InjectMocks
	private JwtAuthorizationFilter jwtAuthorizationFilter;

	private static AuthenticationManager authenticationManager;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private FilterChain filterChain;
	private final String ORG_ID = "test-id";
	private final String ORG_TYPE = "registry";
	private Environment environment;

	@BeforeClass
	public static void setUpStatic() {
		authenticationManager = mock(AuthenticationManager.class);
	}

	@Before
	public void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = mock(FilterChain.class);
		environment = mock(Environment.class);
		jwtAuthorizationFilter = new JwtAuthorizationFilter(authenticationManager, environment);
	}

	@After
	public void tearDown() {
		environment = null;
	}

	@Test
	public void testDoFilterInternal() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withId("test-id")
				.withOrgType(ORG_TYPE);
		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		when(environment.getProperty(Constants.ORGANIZATION_ID_VARIABLE)).thenReturn(ORG_ID);
		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithInvalidOrgId() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withId("invalid-id")
				.withOrgType(ORG_TYPE);
		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		when(environment.getProperty(Constants.ORGANIZATION_ID_VARIABLE)).thenReturn(ORG_ID);
		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithNoOrgId() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withOrgType(ORG_TYPE);
		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		when(environment.getProperty(Constants.ORGANIZATION_ID_VARIABLE)).thenReturn(ORG_ID);
		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithNoOrgType() throws IOException, ServletException {
		JwtPayloadHelper payload = new JwtPayloadHelper()
				.withId(ORG_ID);
		request.addHeader("Authorization", JwtTestHelper.createJwt(payload));

		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		when(environment.getProperty(Constants.ORGANIZATION_ID_VARIABLE)).thenReturn(ORG_ID);
		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	public void testDoFilterInternalWithNoHeader() throws IOException, ServletException {
		PowerMockito.mockStatic(SecurityContextHolder.class);
		SecurityContext mockSecurityContext = PowerMockito.mock(SecurityContext.class);

		PowerMockito.when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);

		jwtAuthorizationFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class));
		verify(SecurityContextHolder.getContext(), times(0)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
	}
}
