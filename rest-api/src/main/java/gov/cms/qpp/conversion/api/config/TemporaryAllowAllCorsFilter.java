package gov.cms.qpp.conversion.api.config;

import java.io.IOException;
import java.time.Instant;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This is a temporary allow-all cors filter. It should be removed before the next prod deployment.
 *
 * @author zack
 */
@Component
public class TemporaryAllowAllCorsFilter extends OncePerRequestFilter {
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		if (Instant.now().isAfter(Instant.parse("2019-12-12T15:12:59.928Z"))) {
			throw new IllegalStateException("Application still deployed using temporary allow-all cors filter");
		}

		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, HEAD");
		response.addHeader("Access-Control-Allow-Headers",
				"Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
		response.addHeader("Access-Control-Expose-Headers",
				"Access-Control-Allow-Origin, Access-Control-Allow-Credentials");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addIntHeader("Access-Control-Max-Age", 10);
		filterChain.doFilter(request, response);
	}
}
