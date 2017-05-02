package gov.cms.qpp.conversion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Help with json comparisons
 */
public class JsonHelper {

	/**
	 * Constructor that is private and empty because this is a utility class.
	 */
	private JsonHelper() {
		//private and empty because this is a utility class
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

	/**
	 * Reads JSON from the {@code InputStream} and returns a subset based on the provided JSONPath.
	 *
	 * See http://goessner.net/articles/JsonPath/
	 *
	 * @param jsonStream An InputStream containing JSON.
	 * @param jsonPath A JSONPath as specified at http://goessner.net/articles/JsonPath/
	 * @param returnType The requested return type as a class.
	 * @param <T> The return type that you want.
	 * @return The requested return type.
	 */
	public static <T> T readJsonAtJsonPath(InputStream jsonStream, String jsonPath, Class<T> returnType) {
		return JsonPath.parse(jsonStream).read(jsonPath, returnType);
	}
}
