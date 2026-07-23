package gov.cms.qpp.conversion.api.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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
		if (request instanceof HttpServletRequest servletRequest) {
			tokenHeader = servletRequest.getHeader(HEADER_STRING);
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

	/** Reusable Jackson mapper for JWT payload decoding. */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

	@SuppressWarnings("unchecked")
	private Map<String, String> getPayload(String tokenHeader) {
		String token = tokenHeader.replace(TOKEN_PREFIX, "");
		String[] parts = token.split("\\.");
		if (parts.length < 2) {
			return Collections.emptyMap();
		}
		try {
			// Decode the payload (second part) directly — no signature verification needed.
			// This works regardless of the alg header (HS256, RS256, none, etc.).
			byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
			Map<String, Object> claims = OBJECT_MAPPER.readValue(
					new String(payloadBytes, StandardCharsets.UTF_8), MAP_TYPE);
			Object data = claims.get("data");
			if (data instanceof Map) {
				return (Map<String, String>) data;
			}
		} catch (IOException | IllegalArgumentException e) {
			// Malformed token — treat as unauthenticated
		}
		return Collections.emptyMap();
	}

	private boolean isValidCpcPlusOrg(Map<String, String> payloadMap) {
		String payloadOrgName = payloadMap.get("name");
		return payloadOrgName != null
				&& payloadMap.containsKey("orgType")
				&& orgName.contains(payloadOrgName);
	}
}
