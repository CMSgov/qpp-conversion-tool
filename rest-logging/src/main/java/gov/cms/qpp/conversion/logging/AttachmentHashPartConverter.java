package gov.cms.qpp.conversion.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;

public class AttachmentHashPartConverter extends ClassicConverter {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(AttachmentHashPartConverter.class);

	@Override
	public String convert(ILoggingEvent event) {
		String hashPart = "";

		try {
			hashPart = getHashPart();
		} catch (Exception e) {
			DEV_LOG.trace("No part to associate with log output.", e);
		}

		return hashPart;
	}

	String getHashPart() throws IOException, ServletException {
		Part part = getPart();
		if (part != null) {
			return String.valueOf(part.hashCode());
		}
		return "";
	}

	Part getPart() throws IOException, ServletException {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs.getRequest().getParts().iterator().next();
	}
}
