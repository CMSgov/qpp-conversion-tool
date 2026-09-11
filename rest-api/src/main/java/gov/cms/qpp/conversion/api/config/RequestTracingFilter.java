package gov.cms.qpp.conversion.api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Filter that generates a unique request ID for each HTTP request.
 * The ID is stored in MDC for inclusion in all log entries and returned
 * in the X-Request-ID response header.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestTracingFilter implements Filter {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String REQUEST_ID_MDC_KEY = "requestId";

    // Only accept UUID-shaped inbound ids to prevent header/log injection via a client-supplied value
    private static final Pattern VALID_REQUEST_ID = Pattern.compile("^[a-fA-F0-9-]{1,64}$");

    @Override
    @SuppressFBWarnings(value = {"BC_UNCONFIRMED_CAST", "HRS_REQUEST_PARAMETER_TO_HTTP_HEADER"},
            justification = "Filter is only ever registered for HTTP requests in this Spring Boot app; "
                    + "the inbound X-Request-ID is validated against VALID_REQUEST_ID before being echoed "
                    + "back, so it cannot carry CRLF/header-injection payloads")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check for existing request ID (forwarded from load balancer/gateway)
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank() || !VALID_REQUEST_ID.matcher(requestId).matches()) {
            requestId = UUID.randomUUID().toString();
        }

        try {
            // Store in MDC for all log statements in this thread
            MDC.put(REQUEST_ID_MDC_KEY, requestId);

            // Add to response header for client correlation
            httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

            chain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent leakage in thread pools
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }
}
