package gov.cms.qpp.conversion.encode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Manages building a "simple" object of JSON conversion.
 * JSON renderers can convert maps and list into JSON Strings.
 * This class is a wrapper around a list/map impl.
 * 
 * @author David Uselmann
 *
 * @param <T> Shole be String or Object for maps of children
 */
public class JsonWrapper {

	public static ObjectWriter getObjectWriter() {
		DefaultIndenter withLinefeed = new DefaultIndenter("  ", "\n");
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(withLinefeed);
		return new ObjectMapper().writer().with(printer);
	}

	private ObjectWriter ow;

	public JsonWrapper() {
		ow = getObjectWriter();
	}

	private Map<String, Object> object;
	private List<Object> list;

	public JsonWrapper putObject(String name, Object value) {
		checkState(list);
		initAsObject();
		value = stripWrapper(value);
		if (value==null) {
			return this;
		}
		this.object.put(name,value);
		return this;
	}

	public Object stripWrapper(Object value) {
		if (value instanceof JsonWrapper) {
			JsonWrapper wrapper = (JsonWrapper) value;
			value = wrapper.getObject();
		}
		return value;
	}

	public JsonWrapper putString(String name, String value) {
		return putObject(name, value);
	}

	public JsonWrapper putDate(String name, String value) throws EncodeException {
		try {
			return putObject(name, validDate(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	public JsonWrapper putInteger(String name, String value) throws EncodeException {
		try {
			return putObject(name, validInteger(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	public JsonWrapper putFloat(String name, String value) throws EncodeException {
		try {
			return putObject(name, validFloat(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	public JsonWrapper putBoolean(String name, String value) throws EncodeException {
		try {
			return putObject(name, validBoolean(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	public JsonWrapper putObject(Object value) {
		checkState(object);
		initAsList();
		value = stripWrapper(value);
		if (value==null) {
			return this;
		}
		this.list.add(value);
		return this;
	}

	public JsonWrapper putString(String value) {
		return putObject(value);
	}

	public JsonWrapper putInteger(String value) throws EncodeException {
		try {
			return putObject( validInteger(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	public JsonWrapper putDate(String value) throws EncodeException {
		try {
			return putObject( validDate(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	public JsonWrapper putFloat(String value) throws EncodeException {
		try {
			return putObject( validFloat(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	public JsonWrapper putBoolean(String value) throws EncodeException {
		try {
			return putObject( validBoolean(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	public String getString(String name) {
		return getValue(name);
	}

	public Integer getInteger(String name) {
		return getValue(name);
	}

	public Float getFloat(String name) {
		return getValue(name);
	}

	public Boolean getBoolean(String name) {
		return getValue(name);
	}

	private <T> T getValue(String name) {
		if (isObject()) {
			return (T)object.get(name);
		}
		return null;
	}

	protected String cleanString(String value) {
		if (value == null) {
			return "";
		}
		return value.trim().toLowerCase();
	}

	protected Integer validInteger(String value) throws EncodeException {
		try {
			return Integer.parseInt( cleanString(value) );
		} catch (Exception e) {
			throw new EncodeException(value + " is not an integer.", e);
		}
	}

	protected String validDate(String value) throws EncodeException {
		try {
			LocalDate thisDate = LocalDate.parse(cleanString(value),  DateTimeFormatter.ofPattern("yyyyMMdd"));
			return thisDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Exception e) {
			throw new EncodeException(value + " is not an date of format YYYYMMDD.", e);
		}
	}

	protected Float validFloat(String value) throws EncodeException {
		try {
			return Float.parseFloat( cleanString(value) );
		} catch (Exception e) {
			throw new EncodeException(value + " is not a number.", e);
		}
	}

	protected Boolean validBoolean(String value) throws EncodeException {
		value = cleanString(value);
		
		if ("true".equals(value) || "yes".equals(value) || "y".equals(value)) {
			return true;
		}
		
		if ("false".equals(value) || "no".equals(value) || "n".equals(value)) {
			return false;
		}
		
		throw new EncodeException(value + " is not a boolean.", null);
	}

	protected void initAsObject() {
		if (object == null) {
			object = new LinkedHashMap<>();
		}
	}

	protected void initAsList() {
		if (list == null) {
			list = new LinkedList<>();
		}
	}

	protected void checkState(Object check) {
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
