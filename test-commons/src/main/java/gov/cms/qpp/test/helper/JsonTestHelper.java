package gov.cms.qpp.test.helper;

import java.io.IOException;
import java.nio.file.Path;

import com.jayway.jsonpath.JsonPath;

public class JsonTestHelper {

	/**
	 * Reads JSON from a file at the specified {@code Path} and returns a subset based on the provided JSONPath.
	 *
	 * See http://goessner.net/articles/JsonPath/
	 *
	 * @param jsonFile A Path to a file containing JSON.
	 * @param jsonPath A JSONPath as specified at http://goessner.net/articles/JsonPath/
	 * @param returnType The requested return type as a class.
	 * @param <T> The return type that you want.
	 * @return The requested return type.
	 */
	public static <T> T readJsonAtJsonPath(Path jsonFile, String jsonPath, Class<T> returnType) throws IOException {
		return JsonPath.parse(jsonFile.toFile()).read(jsonPath, returnType);
	}

	/**
	 * Reads JSON from a file at the specified {@code Path} and returns a subset based on the provided JSONPath.
	 *
	 * See http://goessner.net/articles/JsonPath/
	 *
	 * @param jsonFile A Path to a file containing JSON.
	 * @param jsonPath A JSONPath as specified at http://goessner.net/articles/JsonPath/
	 * @param <T> The return type that you want.
	 * @return The requested return type.
	 */
	public static <T> T readJsonAtJsonPath(Path jsonFile, String jsonPath) throws IOException {
		return JsonPath.parse(jsonFile.toFile()).read(jsonPath);
	}

	private JsonTestHelper() {
	}

}
