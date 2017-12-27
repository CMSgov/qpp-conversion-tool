package gov.cms.qpp.conversion.api.security;

import com.google.common.collect.Sets;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Filter for checking the Json Web Token (JWT) for the correct Authorization
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private static final String HEADER_STRING = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";
	private static final Set<String> VALID_ORG_IDS = Sets.newHashSet("fb1778dd-a5e3-42c8-836f-47654e003fab",
			"ef33b1f6-842a-4519-9109-2ca2ed728ceb", "45624754-cce1-47ea-a7b1-02a9b9e19c2a");

	/**
	 * JWT Constructor with Authentication manager
	 *
	 * @param authManager Object to be passed to it's parent constructor
	 */
	public JwtAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	/**
	 * Internal filter of the Json Web Token (jwt) to determine the organization.
	 *
	 * @param request Object holding the token
	 * @param response Object to hold the parsed token object
	 * @param chain
	 * @throws IOException check for IOException occuring
	 * @throws ServletException check for ServletException occuring
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain) throws IOException, ServletException {
		String tokenHeader = request.getHeader(HEADER_STRING);

		if (tokenHeader == null) {
			chain.doFilter(request, response);
			return;
		}

		Map<String, String> payloadMap = getPayload(tokenHeader);
		if (isValidCpcPlusOrg(payloadMap)) {
			UsernamePasswordAuthenticationToken token =
					new UsernamePasswordAuthenticationToken(payloadMap.get("id"), null , new ArrayList<>());
			SecurityContextHolder.getContext().setAuthentication(token);
		}
		chain.doFilter(request, response);
	}

	/**
	 * Parses the token header into a payload data map
	 *
	 * @param tokenHeader Object holding the token
	 * @return data map of the token parsed
	 */
	private Map<String, String> getPayload(String tokenHeader) {
		String tokenWithoutBearer = tokenHeader.replace(TOKEN_PREFIX, "");
		String tokenWithoutSignatureAndBearer = removeSignature(tokenWithoutBearer);
		Claims body = Jwts.parser()
				.parseClaimsJwt(tokenWithoutSignatureAndBearer)
				.getBody();
		return body.get("data", Map.class);
	}

	/**
	 * Removes signature for JWT parsing.
	 *
	 * @param jws Signed JWT
	 * @return JWT unsigned
	 */
	private String removeSignature(String jws) {
		int i = jws.lastIndexOf('.');
		return jws.substring(0, i + 1);
	}

	/**
	 * Check for the valid cpc+ organization
	 *
	 * @param payloadMap Data map holding the currently parsed user/org
	 * @return validation of the user/org
	 */
	private boolean isValidCpcPlusOrg(Map<String, String> payloadMap) {
		String orgId = payloadMap.get("id");
		return (orgId != null && payloadMap.get("orgType") != null && VALID_ORG_IDS.contains(orgId));
	}
}
