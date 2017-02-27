package gov.cms.qpp.conversion.encode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Manages building a "simple" object of JSON conversion.
 * JSON renderers can convert maps and list into JSON Strings.
 * This class is a wrapper around a list/map impl.
 * 
 * @author daviduselmann
 *
 * @param <T> Shole be String or Object for maps of children
 */
public class JsonWrapper<T> {

	// package access allows for simpler testing
	/*package*/ ObjectWriter ow;
	
	public JsonWrapper() {
		System.setProperty("line.separator", "\n");
		ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	}

	private Map<String, T> object;
	private List<T> list;

	public JsonWrapper<T> put(String name, T value) {
		checkState(list);
		initAsObject();
		this.object.put(name,value);
		return this;
	}
	public JsonWrapper<T> put(T value) {
		checkState(object);
		initAsList();
		this.list.add(value);
		return this;
	}

	private void initAsObject() {
		if (object == null) {
			object = new LinkedHashMap<>();
		}
	}
	
	private void initAsList() {
		if (list == null) {
			list = new LinkedList<>();
		}
	}
	
	private void checkState(Object check) {
		if (check != null) {
			throw new IllegalStateException("Current state may not change (from list to object or reverse).");
		}
	}
	
	public boolean isObject() {
		return object != null;
	}
	
	public Object getObject() {
		return isObject()? object :list;
	}
	
	@Override
	public String toString() {
		try {
			return ow.writeValueAsString(isObject() ?object :list);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}
}
