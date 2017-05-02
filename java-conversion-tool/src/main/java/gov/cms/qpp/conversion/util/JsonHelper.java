package gov.cms.qpp.conversion.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
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

	public static <T> T readJsonAtJsonPath(Path filePath, String jsonPath, Class<T> returnType) throws IOException {
		return JsonPath.parse(filePath).read(jsonPath, returnType);
	}

	public static <T> T readJsonAtJsonPath(InputStream jsonStream, String jsonPath, Class<T> returnType) {
		return JsonPath.parse(jsonStream).read(jsonPath, returnType);
	}

	public static <T, V> T readValueAtJsonPathOnObject(V objectToSearch, String jsonPath, Class<T> returnType) throws IOException {
		ObjectWriter jsonObjectWriter = new ObjectMapper().writer();

		Writer stringWriter = new StringWriter();
		jsonObjectWriter.writeValue(stringWriter, objectToSearch);

		String jsonBlob = stringWriter.toString();

		return JsonPath.parse(jsonBlob).read(jsonPath, returnType);
	}
}
