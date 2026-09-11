package gov.cms.qpp.conversion.api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

/**
 * Structured request logging filter that logs request lifecycle events
 * with rich metadata for observability.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // After RequestTracingFilter
public class StructuredRequestLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(StructuredRequestLoggingFilter.class);

    // MDC keys for structured logging
    private static final String MDC_HTTP_METHOD = "httpMethod";
    private static final String MDC_REQUEST_URI = "requestUri";
    private static final String MDC_REMOTE_ADDR = "remoteAddr";
    private static final String MDC_USER_AGENT = "userAgent";
    private static final String MDC_STATUS_CODE = "statusCode";
    private static final String MDC_DURATION_MS = "durationMs";
    private static final String MDC_REQUEST_STAGE = "requestStage";
    private static final String MDC_PROTOCOL = "protocol";
    private static final String MDC_QUERY_STRING = "queryString";
    private static final String MDC_ATTACHMENT = "attachment";

    @Override
    @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST",
            justification = "Filter is only ever registered for HTTP requests in this Spring Boot app")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        try {
            // Populate MDC with request metadata
            populateRequestMdc(httpRequest);

            // Log STARTED event
            MDC.put(MDC_REQUEST_STAGE, "STARTED");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Request processing started");
            }

            chain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log COMPLETED event with response metadata
            MDC.put(MDC_REQUEST_STAGE, "COMPLETED");
            MDC.put(MDC_STATUS_CODE, String.valueOf(httpResponse.getStatus()));
            MDC.put(MDC_DURATION_MS, String.valueOf(duration));

            LOG.info("Request processing completed");

            // Clean up MDC
            clearRequestMdc();
        }
    }

    private void populateRequestMdc(HttpServletRequest request) {
        MDC.put(MDC_HTTP_METHOD, request.getMethod());
        MDC.put(MDC_REQUEST_URI, request.getRequestURI());
        MDC.put(MDC_REMOTE_ADDR, getClientIpAddress(request));
        MDC.put(MDC_USER_AGENT, sanitizeHeader(request.getHeader("User-Agent")));
        MDC.put(MDC_PROTOCOL, request.getProtocol());

        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isBlank()) {
            MDC.put(MDC_QUERY_STRING, queryString);
        }

        // Safely extract attachment hash if present (multipart); container caches parts so this
        // does not consume the stream before the controller reads it.
        try {
            if (isMultipart(request)) {
                request.getParts().stream()
                        .findFirst()
                        .ifPresent(part -> MDC.put(MDC_ATTACHMENT, String.valueOf(part.hashCode())));
            }
        } catch (Exception e) {
            LOG.debug("Failed to extract attachment hash", e);
        }
    }

    private void clearRequestMdc() {
        MDC.remove(MDC_HTTP_METHOD);
        MDC.remove(MDC_REQUEST_URI);
        MDC.remove(MDC_REMOTE_ADDR);
        MDC.remove(MDC_USER_AGENT);
        MDC.remove(MDC_STATUS_CODE);
        MDC.remove(MDC_DURATION_MS);
        MDC.remove(MDC_REQUEST_STAGE);
        MDC.remove(MDC_PROTOCOL);
        MDC.remove(MDC_QUERY_STRING);
        MDC.remove(MDC_ATTACHMENT);
    }

    private boolean isMultipart(HttpServletRequest request) {
        return request.getContentType() != null
                && request.getContentType().toLowerCase(Locale.ENGLISH).startsWith("multipart/");
    }

    private String getClientIpAddress(HttpServletRequest request) {
        // Check for forwarded IP from load balancer
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // Take first IP in chain (original client)
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String sanitizeHeader(String value) {
        return value == null ? "" : value;
    }
}
