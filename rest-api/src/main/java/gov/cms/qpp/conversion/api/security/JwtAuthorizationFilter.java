package gov.cms.qpp.conversion.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Filter for checking the Json Web Token (JWT) for the correct Authorization
 */
public class JwtAuthorizationFilter implements Filter {
	public static final String DEFAULT_ORG_NAME = "cpc-test";
	public static final String DEFAULT_RTI_ORG = "rti-test";
	public static final Set<String> DEFAULT_ORG_SET = Set.of(DEFAULT_ORG_NAME);
	private static final String HEADER_STRING = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	protected final Set<String> orgName;

	/**
	 * JWT Constructor default
	 */
	public JwtAuthorizationFilter() {
		this(DEFAULT_ORG_SET);
	}

	/**
	 * JWT Constructor with Organization Set
	 *
	 * @param orgName The organization name
	 */
	public JwtAuthorizationFilter(Set<String> orgName) {
		this.orgName = orgName;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = null;
		String tokenHeader = null;
		if (request instanceof HttpServletRequest) {
			httpRequest = (HttpServletRequest) request;
			tokenHeader = httpRequest.getHeader(HEADER_STRING);
		}

		if (tokenHeader == null) {
			chain.doFilter(request, response);
			return;
		}

		Map<String, String> payloadMap = getPayload(tokenHeader);
		if (isValidCpcPlusOrg(payloadMap)) {
			UsernamePasswordAuthenticationToken token =
					new UsernamePasswordAuthenticationToken(payloadMap.get("id"), null, new ArrayList<>());
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
	@SuppressWarnings("unchecked")
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
		String payloadOrgName = payloadMap.get("name");
		return (payloadOrgName != null && payloadMap.containsKey("orgType") && orgName.contains(payloadOrgName));
	}
}