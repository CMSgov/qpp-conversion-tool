package gov.cms.qpp.conversion.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

	/** Immutable copy of the allowed organization names */
	private final Set<String> orgName;

	/**
	 * JWT Constructor default
	 */
	public JwtAuthorizationFilter() {
		this(DEFAULT_ORG_SET);
	}

	/**
	 * JWT Constructor with Organization Set
	 *
	 * @param orgName The organization names
	 */
	public JwtAuthorizationFilter(Set<String> orgName) {
		// Defensively copy to prevent external mutation
		this.orgName = Collections.unmodifiableSet(Set.copyOf(orgName));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String tokenHeader = null;
		if (request instanceof HttpServletRequest) {
			tokenHeader = ((HttpServletRequest) request).getHeader(HEADER_STRING);
		}

		if (tokenHeader != null) {
			Map<String, String> payloadMap = getPayload(tokenHeader);
			if (isValidCpcPlusOrg(payloadMap)) {
				UsernamePasswordAuthenticationToken token =
						new UsernamePasswordAuthenticationToken(payloadMap.get("id"), null, new ArrayList<>());
				SecurityContextHolder.getContext().setAuthentication(token);
			}
		}

		chain.doFilter(request, response);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getPayload(String tokenHeader) {
		String tokenWithoutBearer = tokenHeader.replace(TOKEN_PREFIX, "");
		String tokenWithoutSignatureAndBearer = removeSignature(tokenWithoutBearer);
		Claims body = Jwts.parser()
				.parseClaimsJwt(tokenWithoutSignatureAndBearer)
				.getBody();
		return body.get("data", Map.class);
	}

	private String removeSignature(String jws) {
		int i = jws.lastIndexOf('.');
		return jws.substring(0, i + 1);
	}

	private boolean isValidCpcPlusOrg(Map<String, String> payloadMap) {
		String payloadOrgName = payloadMap.get("name");
		return payloadOrgName != null
				&& payloadMap.containsKey("orgType")
				&& orgName.contains(payloadOrgName);
	}
}
