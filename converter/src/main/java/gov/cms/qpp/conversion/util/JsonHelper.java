package gov.cms.qpp.conversion.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configuration.Defaults;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

/**
 * Help with json comparisons
 */
public class JsonHelper {

	private static final String PROBLEM_PARSING_JSON = "Problem parsing json string";

	static {
		Configuration.setDefaults(new Defaults() {
			@Override
			public JsonProvider jsonProvider() {
				return new JacksonJsonProvider();
			}

			@Override
			public MappingProvider mappingProvider() {
				return new JacksonMappingProvider();
			}

			@Override
			public Set<Option> options() {
				return Collections.emptySet();
			}			
		});
	}

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
		return new ObjectMapper().readValue(Files.newBufferedReader(filePath), valueType);
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
	 * @throws IOException if problems arise while attempting to parse the json file
	 */
	public static <T> T readJson(Path filePath, TypeReference<T> valueType) throws IOException {
		return new ObjectMapper().readValue(Files.newBufferedReader(filePath), valueType);
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
	public static <T> T readJsonAtJsonPath(InputStream jsonStream, String jsonPath, TypeRef<T> returnType) {
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
	public static <T> T readJsonAtJsonPath(String json, String jsonPath, TypeRef<T> returnType) {
		return JsonPath.parse(json).read(jsonPath, returnType);
	}
}
