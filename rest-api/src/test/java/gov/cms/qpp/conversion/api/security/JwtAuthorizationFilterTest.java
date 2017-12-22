package gov.cms.qpp.conversion.api.security;

import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;

import static org.mockito.Mockito.mock;

public class JwtAuthorizationFilterTest {

	private static AuthenticationManager authenticationManager;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockFilterChain filterChain;
	private static final String TEST_TOKEN_HEADER = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkYXRhIjp7Im9yZ1R5cGUiOiJyZWdpc3RyeSIsImNtc0lkIjo0MDAwMCwicHJvZ3JhbVllYXIiOjIwMTcsImlkIjoibXVzaHJvb21zIiwibmFtZSI6InRlc3Rpbmctb3JnIn0sImV4cCI6MTUxMzk4NjE3NiwiaXNzIjoidGVzdGluZy1vcmciLCJqdGkiOiJkZjdiNGY3NS05Y2NiLTRkNjQtYjhkNC1lMjVlYzNiMjJkZTUiLCJpYXQiOjE1MTM5ODI1NzZ9.yEGPz-OzaDMwMVAuvAktodhzRpN57zxZA8HwX8olBow";


	@BeforeAll
	static void setUpStatic() {
		authenticationManager = mock(AuthenticationManager.class);
	}

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
	}

	@Test
	void testDoFilterInternal() throws IOException, ServletException {
		request.addHeader("Authorization", TEST_TOKEN_HEADER);
		JwtAuthorizationFilter testJwtAuthFilter = new JwtAuthorizationFilter(authenticationManager);
		testJwtAuthFilter.doFilterInternal(request, response, filterChain);
	}
}
