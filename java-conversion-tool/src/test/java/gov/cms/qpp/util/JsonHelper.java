package gov.cms.qpp.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Help with json comparisons
 */
public class JsonHelper {
	/**
	 * Read json file and return an object representation of its content
	 *
	 * @param filePath json file path
	 * @return hashed json structure
	 * @throws IOException
	 */
	public static HashMap<String,Object> readJson(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		return (HashMap<String,Object>) new ObjectMapper().readValue(path.toFile(), HashMap.class);
	}
}
