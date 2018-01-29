package gov.cms.qpp.conversion.api.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;

/**
 * Adds a logging field that identifies the uploaded file as part of an current request.
 */
public class AttachmentHashPartConverter extends ClassicConverter {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(AttachmentHashPartConverter.class);

	/**
	 * Given the logging event, spits out an object hashcode string associated with the file that was uploaded for the current request.
	 *
	 * @param event The event to add additional detail to.
	 * @return The hash from the uploaded file.
	 */
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

	/**
	 * Returns the Java object hashcode of the uploaded file for the current request.
	 *
	 * @return A string of the Java object hashcode.
	 * @throws IOException If getting the parts fail.
	 * @throws ServletException If getting the parts fail.
	 */
	String getHashPart() throws IOException, ServletException {
		Part part = getPart();
		if (part != null) {
			return String.valueOf(part.hashCode());
		}
		return "";
	}

	/**
	 * Get's the uploaded file for the current request.
	 *
	 * @return The uploaded file for the current request.
	 * @throws IOException If getting the parts fail.
	 * @throws ServletException If getting the parts fail.
	 */
	Part getPart() throws IOException, ServletException {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs.getRequest().getParts().iterator().next();
	}
}
