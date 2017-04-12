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
 * @param <T> Shall be String or Object for maps of children
 */
public class JsonWrapper {

	private ObjectWriter ow;
	private Map<String, Object> object;
	private List<Object> list;

	public JsonWrapper() {
		ow = getObjectWriter();
	}

	/**
	 * Static factory that creates {@link com.fasterxml.jackson.databind.ObjectWriter}s.
	 *
	 * @return {@link com.fasterxml.jackson.databind.ObjectWriter}
	 */
	public static ObjectWriter getObjectWriter() {
		DefaultIndenter withLinefeed = new DefaultIndenter("  ", "\n");
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(withLinefeed);
		return new ObjectMapper().writer().with(printer);
	}

	/**
	 * Places a named object within the wrapper. In the event the named object is
	 * also a {@link gov.cms.qpp.conversion.encode.JsonWrapper} its wrapped
	 * content will be extracted.
	 *
	 * Think of this as adding an attribute to a JSON hash.
	 *
	 * @param name {@link String}
	 * @param value {@link String}
	 * @return {@link JsonWrapper}
	 */
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

	/**
	 * Extract wrapped content from a {@link gov.cms.qpp.conversion.encode.JsonWrapper}.
	 *
	 * @param value {@link Object}
	 * @return {@link Object}
	 */
	public Object stripWrapper(Object value) {
		if (value instanceof JsonWrapper) {
			JsonWrapper wrapper = (JsonWrapper) value;
			value = wrapper.getObject();
		}
		return value;
	}

	/**
	 * Places a named String within the wrapper. See {@link #putObject(String, Object)}
	 *
	 * @param name {@link String}
	 * @param value {@link String}
	 * @return {@link JsonWrapper}
	 */
	public JsonWrapper putString(String name, String value) {
		return putObject(name, value);
	}

	/**
	 * Places a named String that represents a date within the wrapper. See {@link #putObject(String, Object)}
	 *
	 * @param name {@link String}
	 * @param value {@link String} must conform with {@link #validDate(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putDate(String name, String value) throws EncodeException {
		try {
			return putObject(name, validDate(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places a named String that represents an {@link java.lang.Integer} within the wrapper.
	 * See {@link #putObject(String, Object)}
	 *
	 * @param name {@link String}
	 * @param value {@link String} must conform with {@link #validInteger(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putInteger(String name, String value) throws EncodeException {
		try {
			return putObject(name, validInteger(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a date within the wrapper. See {@link #putObject(Object)}
	 *
	 * @param value {@link String} must conform with {@link #validDate(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putDate(String value) throws EncodeException {
		try {
			return putObject( validDate(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places an named String that represents a {@link java.lang.Float} within the wrapper.
	 * See {@link #putObject(String, Object)}
	 *
	 * @param name {@link String}
	 * @param value {@link String} must conform with {@link #validFloat(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putFloat(String name, String value) throws EncodeException {
		try {
			return putObject(name, validFloat(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places a named String that represents a {@link java.lang.Boolean} within the wrapper.
	 * See {@link #putObject(String, Object)}
	 *
	 * @param name {@link String}
	 * @param value {@link String} must conform with {@link #validBoolean(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putBoolean(String name, String value) throws EncodeException {
		try {
			return putObject(name, validBoolean(value) );
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places an unnamed {@link java.lang.Object} within the wrapper. In the event the named object is
	 * also a {@link gov.cms.qpp.conversion.encode.JsonWrapper} its wrapped content will be extracted.
	 *
	 * Think of this as adding a JSON array entry.
	 *
	 * @param value {@link String}
	 * @return {@link JsonWrapper}
	 */
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

	/**
	 * Places an unnamed String within the wrapper.
	 * See {@link #putObject(Object)}
	 *
	 * @param value {@link String}
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putString(String value) {
		return putObject(value);
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Integer} within the wrapper.
	 * See {@link #putObject(Object)}
	 *
	 * @param value {@link String} must conform with {@link #validInteger(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putInteger(String value) throws EncodeException {
		try {
			return putObject( validInteger(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Float} within the wrapper.
	 * See {@link #putObject(Object)}
	 *
	 * @param value {@link String} must conform with {@link #validFloat(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putFloat(String value) throws EncodeException {
		try {
			return putObject( validFloat(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Boolean} within the wrapper.
	 * See {@link #putObject(Object)}
	 *
	 * @param value {@link String} must conform with {@link #validBoolean(String)} validation
	 * @return {@link JsonWrapper}
	 * @throws EncodeException
	 */
	public JsonWrapper putBoolean(String value) throws EncodeException {
		try {
			return putObject( validBoolean(value) );
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Retrieve a named {@link String} from the {@link JsonWrapper}.
	 * See {@link #getValue(String)}
	 *
	 * @param name {@link String} name of the wrapped value
	 * @return {@link String}
	 */
	public String getString(String name) {
		return getValue(name);
	}

	/**
	 * Retrieve a named {@link Integer} from the {@link JsonWrapper}.
	 * See {@link #getValue(String)}
	 *
	 * @param name {@link String} name of the wrapped value
	 * @return {@link Integer}
	 */
	public Integer getInteger(String name) {
		return getValue(name);
	}

	/**
	 * Retrieve a named {@link Float} from the {@link JsonWrapper}.
	 * See {@link #getValue(String)}
	 *
	 * @param name {@link String} name of the wrapped value
	 * @return {@link Float}
	 */
	public Float getFloat(String name) {
		return getValue(name);
	}

	/**
	 * Retrieve a named {@link Boolean} from the {@link JsonWrapper}.
	 * See {@link #getValue(String)}
	 *
	 * @param name {@link String} name of the wrapped value
	 * @return {@link Boolean}
	 */
	public Boolean getBoolean(String name) {
		return getValue(name);
	}

	/**
	 * Return the named value from the {@link JsonWrapper}.
	 *
	 * Think of this as retrieval of a JSON hash attribute
	 *
	 * @param name {@link String}
	 * @param <T>
	 * @return T
	 */
	private <T> T getValue(String name) {
		if (isObject()) {
			return (T)object.get(name);
		}
		return null;
	}

	/**
	 * Enforces uniform {@link String} presentation.
	 *
	 * @param value {@link String}
	 * @return {@link String}
	 */
	protected String cleanString(String value) {
		if (value == null) {
			return "";
		}
		return value.trim().toLowerCase();
	}

	/**
	 * Validates that the given value is an parsable integer.
	 *
	 * @param value {@link String}
	 * @return {@link Integer}
	 * @throws EncodeException
	 */
	protected Integer validInteger(String value) throws EncodeException {
		try {
			return Integer.parseInt( cleanString(value) );
		} catch (Exception e) {
			throw new EncodeException(value + " is not an integer.", e);
		}
	}

	/**
	 * Validates that the given value conforms to expected date formatting (i.e. yyyyMMdd).
	 *
	 * @param value {@link String}
	 * @return {@link String}
	 * @throws EncodeException
	 */
	protected String validDate(String value) throws EncodeException {
		try {
			LocalDate thisDate = LocalDate.parse(cleanString(value),  DateTimeFormatter.ofPattern("yyyyMMdd"));
			return thisDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (Exception e) {
			throw new EncodeException(value + " is not an date of format YYYYMMDD.", e);
		}
	}

	/**
	 * Validates that the given value is an parsable numeric value.
	 *
	 * @param value {@link String}
	 * @return {@link Float}
	 * @throws EncodeException
	 */
	protected Float validFloat(String value) throws EncodeException {
		try {
			return Float.parseFloat( cleanString(value) );
		} catch (Exception e) {
			throw new EncodeException(value + " is not a number.", e);
		}
	}

	/**
	 * Validates that the given value is passable as a {@link Boolean}.
	 *
	 * @param value {@link String}
	 * @return {@link Boolean}
	 * @throws EncodeException
	 */
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

	/**
	 * Determines {@link JsonWrapper}'s intended use as a representation of a JSON hash
	 */
	protected void initAsObject() {
		if (object == null) {
			object = new LinkedHashMap<>();
		}
	}

	/**
	 * Determines {@link JsonWrapper}'s intended use as a representation of a JSON array
	 */
	protected void initAsList() {
		if (list == null) {
			list = new LinkedList<>();
		}
	}

	/**
	 * Helps enforce the initialized representation of the {@link JsonWrapper} as a hash or an array.
	 *
	 * @param check {@link Object}
	 */
	protected void checkState(Object check) {
		if (check != null) {
			throw new IllegalStateException("Current state may not change (from list to object or reverse).");
		}
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content is a hash or array.
	 *
	 * @return boolean
	 */
	public boolean isObject() {
		return object != null;
	}

	/**
	 * Accessor for the content wrapped by the {@link JsonWrapper}.
	 *
	 * @return {@link Object}
	 */
	public Object getObject() {
		return isObject()? object : list;
	}

	/**
	 * String representation of the {@link JsonWrapper}.
	 *
	 * @return {@link String}
	 */
	@Override
	public String toString() {
		try {
			return ow.writeValueAsString(isObject() ?object :list);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}
}
