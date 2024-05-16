package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Utility that help clone target objects
 */
public class CloneHelper {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(Converter.class);

	private CloneHelper(){}

	/**
	 * Create deep copy of given object
	 *
	 * @param in to clone
	 * @param <T> object type
	 * @return clone
	 */
	public static <T> T deepClone(final T in) {
		T copy;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(outputStream);
			output.writeObject(in);
			output.close();

			ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			ObjectInputStream input = new ObjectInputStream(inputStream);
			copy = (T) input.readObject();
			input.close();

		} catch(IOException ex) {
			DEV_LOG.error("Error cloning object - " + ex.getMessage(), ex);
			throw new UncheckedIOException(ex);
		} catch(ClassNotFoundException ex) {
			DEV_LOG.error("Error cloning object - " + ex.getMessage(), ex);
			throw new RuntimeException(ex);
		}
		return copy;
	}
}