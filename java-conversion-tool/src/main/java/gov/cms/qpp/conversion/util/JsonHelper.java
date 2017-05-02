package gov.cms.qpp.conversion.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Help with json comparisons
 */
public class JsonHelper {
	/**
	 * Read json file and return object type specified
	 *
	 * @param filePath json file path
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws IOException
	 */
	public static <T> T readJson(String filePath, Class<T> valueType) throws IOException {
		Path path = Paths.get(filePath);
		return readJson(path, valueType);
	}

	/**
	 * Read json file and return object type specified
	 *
	 * @param filePath json file path
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws IOException
	 */
	public static <T> T readJson(Path filePath, Class<T> valueType) throws IOException {
		return new ObjectMapper().readValue(filePath.toFile(), valueType);
	}
}
