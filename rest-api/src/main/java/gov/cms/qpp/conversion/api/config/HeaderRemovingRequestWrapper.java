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
        // Normalize header names to lowercase (using ROOT locale) for case-insensitive match
        for (String header : headersToRemove) {
            this.headersToRemove.add(header.toLowerCase(Locale.ROOT));
        }
    }

    @Override
    public String getHeader(String name) {
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
        headerNames.removeIf(name -> headersToRemove.contains(name.toLowerCase(Locale.ROOT)));
        return Collections.enumeration(headerNames);
    }
}
