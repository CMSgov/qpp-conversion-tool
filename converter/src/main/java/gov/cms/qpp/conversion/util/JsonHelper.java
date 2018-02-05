package gov.cms.qpp.conversion.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Help with json comparisons
 */
public class JsonHelper {

	private static final String PROBLEM_PARSING_JSON = "Problem parsing json string";

	/**
	 * Constructor that is private and empty because this is a utility class.
	 */
	private JsonHelper() {
		//private and empty because this is a utility class
	}

	/**
	 * Read json and return object type specified
	 *
	 * @param json content
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws JsonReadException if problems arise while attempting to parse the json string
	 */
	public static <T> T readJson(String json, Class<T> valueType) {
		T returnValue;
		try {
			returnValue = new ObjectMapper().readValue(json, valueType);
		} catch (IOException ex) {
			throw new JsonReadException(PROBLEM_PARSING_JSON, ex);
		}
		return returnValue;
	}

	/**
	 * Read json file and return object type specified
	 *
	 * @param filePath json file path
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws IOException if problems arise while attempting to parse the resource at the given filePath
	 */
	public static <T> T readJson(Path filePath, Class<T> valueType) throws IOException {
		return new ObjectMapper().readValue(filePath.toFile(), valueType);
	}

	/**
	 * Read json and return object type specified
	 *
	 * @param json content
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws JsonReadException if problems arise while attempting to parse the json input stream
	 */
	public static <T> T readJson(InputStream json, Class<T> valueType) {
		T returnValue;
		try {
			returnValue = new ObjectMapper().readValue(json, valueType);
		} catch (IOException ex) {
			throw new JsonReadException(PROBLEM_PARSING_JSON, ex);
		}
		return returnValue;
	}

	/**
	 * Read json file and return object type specified
	 *
	 * @param json content
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws JsonReadException if problems arise while attempting to parse the json input stream
	 */
	public static <T> T readJson(InputStream json, TypeReference<T> valueType) {
		T returnValue;
		try {
			returnValue = new ObjectMapper().readValue(json, valueType);
		} catch (IOException ex) {
			throw new JsonReadException(PROBLEM_PARSING_JSON, ex);
		}
		return returnValue;
	}

	/**
	 * Read json file and return object type specified
	 *
	 * @param filePath path to json file
	 * @param valueType object type representation
	 * @param <T> generic class type
	 * @return Object of specified type
	 * @throws JsonReadException if problems arise while attempting to parse the json input stream
	 */
	public static <T> T readJson(Path filePath, TypeReference<T> valueType) throws IOException {
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

	/**
	 * Reads JSON in the passed in {@code String} and returns a subset based on the provided JSONPath.
	 *
	 * See http://goessner.net/articles/JsonPath/
	 *
	 * @param json A String containing JSON.
	 * @param jsonPath A JSONPath as specified at http://goessner.net/articles/JsonPath/
	 * @param returnType The requested return type as a class.
	 * @param <T> The return type that you want.
	 * @return The requested return type.
	 */
	public static <T> T readJsonAtJsonPath(String json, String jsonPath, Class<T> returnType) {
		return JsonPath.parse(json).read(jsonPath, returnType);
	}
}
