package gov.cms.qpp.conversion.api.security;

import gov.cms.qpp.conversion.api.model.security.Organization;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Filter for checking the Json Web Token (JWT) for the correct Authorization
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	private static final String HEADER_STRING = "Authorization";
	private static final String TOKEN_PREFIX = "Bearer ";

	/**
	 * JWT Constructor with Authentication manager
	 *
	 * @param authManager
	 */
	public JwtAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param chain
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain chain) throws IOException, ServletException {
		String header = request.getHeader(HEADER_STRING);

		if (header == null) {
			chain.doFilter(request, response);
			return;
		}

		Organization organization = getOrganization(request);

		if (organization.getId() != null && organization.getOrgType() != null) {

		}
		//SecurityContextHolder.getContext().setAuthentication(organization); //--Holds the information in Security Context for later use?

		//Do some kind of Org authentication or pass it back in the response?

		chain.doFilter(request, response);
	}

	/**
	 *
	 * @param request
	 * @return
	 */
	private Organization getOrganization(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			Claims body = Jwts.parser()
					.setSigningKey("secret".getBytes())
					.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody();

			Organization org = new Organization(body.getId(), (String)body.get("orgType"));
			//return new UsernamePasswordAuthenticationToken(org, null, new ArrayList<>());
			return org;
		}
		return null;
	}
}
