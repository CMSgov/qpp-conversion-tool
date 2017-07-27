package gov.cms.qpp.conversion.api.helper;


import ch.qos.logback.classic.pattern.ThreadConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.catalina.connector.RequestFacade;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Part;

public class ThreadRequestPartConverter extends ThreadConverter{

	@Override
	public String convert(ILoggingEvent event) {
		String threadId = super.convert(event);
		ServletRequestAttributes attrs =
				(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		try {
			RequestFacade facade = ((RequestFacade) attrs.getRequest());
			Part part = facade.getParts().iterator().next();
			if (part != null) {
				threadId = threadId + " - AttachmentId:" + part.hashCode();
			}
		} catch (Exception e) {
			//intentionally ignored
		}

		return threadId;
	}
}
