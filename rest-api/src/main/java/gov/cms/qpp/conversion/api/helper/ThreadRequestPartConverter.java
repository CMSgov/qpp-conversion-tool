package gov.cms.qpp.conversion.api.helper;


import ch.qos.logback.classic.pattern.ThreadConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;

public class ThreadRequestPartConverter extends ThreadConverter {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(ThreadRequestPartConverter.class);

	@Override
	public String convert(ILoggingEvent event) {
		String threadId = super.convert(event);

		try {
			threadId = appendPart(threadId);
		} catch (Exception e) {
			DEV_LOG.trace("No part to associate with log output.", e);
		}

		return threadId;
	}

	String appendPart(String threadId) throws IOException, ServletException {
		Part part = getPart();
		if (part != null) {
			return threadId + " - AttachmentId:" + part.hashCode();
		}
		return threadId;
	}

	Part getPart() throws IOException, ServletException {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs.getRequest().getParts().iterator().next();
	}
}
