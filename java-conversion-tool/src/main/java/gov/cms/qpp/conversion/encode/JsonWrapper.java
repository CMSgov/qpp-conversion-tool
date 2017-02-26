package gov.cms.qpp.conversion.encode;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonWrapper {

	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

	private Map<String, Object> children = new LinkedHashMap<>();
	

	public JsonWrapper put(String name, String value) {
		this.children.put(name,value);
		
		
		return this;
	}
	
	@Override
	public String toString() {
		try {
			return ow.writeValueAsString(children);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}
}
