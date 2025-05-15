package gov.cms.qpp.conversion.api.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Enumeration;
import java.util.Collections;

public class HeaderRemovingRequestWrapper extends HttpServletRequestWrapper {

    private final Set<String> headersToRemove;

    public HeaderRemovingRequestWrapper(HttpServletRequest request, Set<String> headersToRemove) {
        super(request);
        this.headersToRemove = new HashSet<>();
        // Normalize header names to lowercase for case-insensitive match
        for (String header : headersToRemove) {
            this.headersToRemove.add(header.toLowerCase());
        }
    }

    @Override
    public String getHeader(String name) {
        if (headersToRemove.contains(name.toLowerCase())) {
            return null;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (headersToRemove.contains(name.toLowerCase())) {
            return Collections.emptyEnumeration();
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> headerNames = Collections.list(super.getHeaderNames());
        headerNames.removeIf(name -> headersToRemove.contains(name.toLowerCase()));
        return Collections.enumeration(headerNames);
    }
}
