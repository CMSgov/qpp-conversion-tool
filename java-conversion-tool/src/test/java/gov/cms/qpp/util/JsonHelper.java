package gov.cms.qpp.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class JsonHelper {
	public static HashMap<String,Object> readJson(String filePath) throws IOException {
		Path path = Paths.get(filePath);
		return (HashMap<String,Object>) new ObjectMapper().readValue(path.toFile(), HashMap.class);
	}
}
