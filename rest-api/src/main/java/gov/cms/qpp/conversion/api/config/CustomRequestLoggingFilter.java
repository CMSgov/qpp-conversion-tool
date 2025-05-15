package gov.cms.qpp.conversion.api.config;

import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;

/*
 * CommonsRequestLoggingFilter does not provide any way to modify the data logged, 
 *  so we need to overwrite its logic at the message setup stage.
*/
public class CustomRequestLoggingFilter extends CommonsRequestLoggingFilter {
    // Include here any headers that should not be logged to services like Splunk.
    private static final Set<String> SENSITIVE_HEADERS = Set.of("authorization", "cookie");
    
    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        HttpServletRequest sanitizedRequest =  new HeaderRemovingRequestWrapper(request, SENSITIVE_HEADERS);
        
        return super.createMessage(sanitizedRequest, prefix, suffix);
    }
}
