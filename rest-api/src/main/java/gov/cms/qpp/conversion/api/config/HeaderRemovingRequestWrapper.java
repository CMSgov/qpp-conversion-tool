package gov.cms.qpp.conversion.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Locale;

public class HeaderRemovingRequestWrapper extends HttpServletRequestWrapper {

    private final Set<String> headersToRemove;

    public HeaderRemovingRequestWrapper(HttpServletRequest request, Set<String> headersToRemove) {
        super(request);
        this.headersToRemove = new HashSet<>();
        // Normalize header names using a locale‐neutral form for case‐insensitive comparisons
        for (String header : headersToRemove) {
            this.headersToRemove.add(header.toLowerCase(Locale.ROOT));
        }
    }

    @Override
    public String getHeader(String name) {
        // Use Locale.ROOT to avoid locale‐specific case mappings
        if (headersToRemove.contains(name.toLowerCase(Locale.ROOT))) {
            return null;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headersToRemove.contains(name.toLowerCase(Locale.ROOT))) {
            return Collections.emptyEnumeration();
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> headerNames = Collections.list(super.getHeaderNames());
        // Filter out removed headers, using locale‐neutral lowercase
        headerNames.removeIf(header -> headersToRemove.contains(header.toLowerCase(Locale.ROOT)));
        return Collections.enumeration(headerNames);
    }
}
