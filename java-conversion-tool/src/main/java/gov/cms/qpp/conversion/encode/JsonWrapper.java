package gov.cms.qpp.conversion.encode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages building a "simple" object of JSON conversion.
 * JSON renderers can convert maps and list into JSON Strings.
 * This class is a wrapper around a list/map impl.
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
	 * @return utility that will allow client to serialize wrapper contents as json
	 */
	public static ObjectWriter getObjectWriter() {
		DefaultIndenter withLinefeed = new DefaultIndenter("  ", "\n");
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(withLinefeed);
		return new ObjectMapper().writer().with(printer);
	}

	/**
	 * Extract wrapped content from a {@link gov.cms.qpp.conversion.encode.JsonWrapper}.
	 *
	 * @param value {@link Object} which may be wrapped
	 * @return wrapped content
	 */
	public Object stripWrapper(Object value) {
		Object internalValue = value;
		if (value instanceof JsonWrapper) {
			JsonWrapper wrapper = (JsonWrapper) value;
			internalValue = wrapper.getObject();
		}
		return internalValue;
	}

	/**
	 * Places a named String within the wrapper. See {@link #putObject(String, Object)}
	 *
	 * @param name key for value
	 * @param value keyed value
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putString(String name, String value) {
		return putObject(name, value);
	}

	/**
	 * Places an unnamed String within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putString(String value) {
		return putObject(value);
	}

	/**
	 * Places a named String that represents a date within the wrapper. See {@link #putObject(String, Object)}
	 *
	 * @param name key for value
	 * @param value keyed value which must conform with {@link #validDate(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putDate(String name, String value) {
		try {
			return putObject(name, validDate(value));
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a date within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value that must conform with {@link #validDate(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putDate(String value) {
		try {
			return putObject(validDate(value));
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places a named String that represents an {@link java.lang.Integer} within the wrapper.
	 *
	 * @see #putObject(String, Object)
	 * @param name key for value
	 * @param value keyed value which must conform with {@link #validInteger(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putInteger(String name, String value) {
		try {
			return putObject(name, validInteger(value));
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Integer} within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value {@link String} must conform with {@link #validInteger(String)} validation
	 * @return {@link JsonWrapper}
	 */
	public JsonWrapper putInteger(String value) {
		try {
			return putObject(validInteger(value));
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places an named String that represents a {@link java.lang.Float} within the wrapper.
	 *
	 * @see #putObject(String, Object)
	 * @param name key for value
	 * @param value keyed value that must conform with {@link #validFloat(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putFloat(String name, String value) {
		try {
			return putObject(name, validFloat(value));
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Float} within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value that must conform with {@link #validFloat(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putFloat(String value) {
		try {
			return putObject(validFloat(value));
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places a named String that represents a {@link java.lang.Boolean} within the wrapper.
	 *
	 * @see #putObject(String, Object)
	 * @param name key for value
	 * @param value keyed value that must conform with {@link #validBoolean(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putBoolean(String name, String value) {
		try {
			return putObject(name, validBoolean(value));
		} catch (EncodeException e) {
			putObject(name, value);
			throw e;
		}
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Boolean} within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value that must conform with {@link #validBoolean(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putBoolean(String value) {
		try {
			return putObject(validBoolean(value));
		} catch (EncodeException e) {
			putObject(value);
			throw e;
		}
	}

	/**
	 * Places a named object within the wrapper. In the event the named object is
	 * also a {@link gov.cms.qpp.conversion.encode.JsonWrapper} its wrapped
	 * content will be extracted.
	 *
	 * Think of this as adding an attribute to a JSON hash.
	 *
	 * @param name key for value
	 * @param value keyed value
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putObject(String name, Object value) {
		checkState(list);
		initAsObject();
		Object internalValue = stripWrapper(value);
		if (internalValue == null) {
			return this;
		}
		this.object.put(name, internalValue);
		return this;
	}

	/**
	 * Places an unnamed {@link java.lang.Object} within the wrapper. In the event the named object is
	 * also a {@link gov.cms.qpp.conversion.encode.JsonWrapper} its wrapped content will be extracted.
	 *
	 * Think of this as adding a JSON array entry.
	 *
	 * @param value object to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putObject(Object value) {
		checkState(object);
		initAsList();
		Object internalValue = stripWrapper(value);
		if (internalValue == null) {
			return this;
		}
		this.list.add(internalValue);
		return this;
	}

	/**
	 * Retrieve a named {@link String} from the {@link JsonWrapper}.
	 *
	 * @see #getValue(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public String getString(String name) {
		return getValue(name);
	}

	/**
	 * Retrieve a named {@link Integer} from the {@link JsonWrapper}.
	 *
	 * @see #getValue(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public Integer getInteger(String name) {
		return getValue(name);
	}

	/**
	 * Retrieve a named {@link Float} from the {@link JsonWrapper}.
	 *
	 * @see #getValue(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public Float getFloat(String name) {
		return getValue(name);
	}

	/**
	 * Retrieve a named {@link Boolean} from the {@link JsonWrapper}.
	 *
	 * @see #getValue(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public Boolean getBoolean(String name) {
		return getValue(name);
	}

	/**
	 * Return the named value from the {@link JsonWrapper}.
	 *
	 * Think of this as retrieval of a JSON hash attribute
	 *
	 * @param name key for value
	 * @param <T>
	 * @return T retrieved keyed value
	 */
	@SuppressWarnings("unchecked")
	private <T> T getValue(String name) {
		if (isObject()) {
			return (T) object.get(name);
		}
		return null;
	}

	/**
	 * Enforces uniform {@link String} presentation.
	 *
	 * @param value potentially dirty value
	 * @return cleaned
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
	 * @param value to validate
	 * @return valid Integer
	 * @throws EncodeException
	 */
	protected int validInteger(String value) {
		try {
			return Integer.parseInt(cleanString(value));
		} catch (Exception e) {
			throw new EncodeException(value + " is not an integer.", e);
		}
	}

	/**
	 * Validates that the given value conforms to expected date formatting (i.e. yyyyMMdd).
	 *
	 * @param value to validate
	 * @return valid date String
	 * @throws EncodeException
	 */
	protected String validDate(String value) {
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
	 * @param value to validate
	 * @return valid Float value
	 * @throws EncodeException
	 */
	protected float validFloat(String value) {
		try {
			return Float.parseFloat(cleanString(value));
		} catch (Exception e) {
			throw new EncodeException(value + " is not a number.", e);
		}
	}

	/**
	 * Validates that the given value is passable as a {@link Boolean}.
	 *
	 * @param value to validate
	 * @return valid Boolean
	 * @throws EncodeException
	 */
	protected boolean validBoolean(String value) {
		String cleanedValueString = cleanString(value);
		
		if ("true".equals(cleanedValueString) || "yes".equals(cleanedValueString) || "y".equals(cleanedValueString)) {
			return true;
		}
		
		if ("false".equals(cleanedValueString) || "no".equals(cleanedValueString) || "n".equals(cleanedValueString)) {
			return false;
		}
		
		throw new EncodeException(cleanedValueString + " is not a boolean.", null);
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
	 * @param check should be null
	 */
	protected void checkState(Object check) {
		if (check != null) {
			throw new IllegalStateException("Current state may not change (from list to object or reverse).");
		}
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content is a hash or array.
	 *
	 * @return boolean is this a JSON object
	 */
	public boolean isObject() {
		return object != null;
	}

	/**
	 * Accessor for the content wrapped by the {@link JsonWrapper}.
	 *
	 * @return wrapped content
	 */
	public Object getObject() {
		return isObject() ? object : list;
	}

	/**
	 * String representation of the {@link JsonWrapper}.
	 *
	 * @return
	 */
	@Override
	public String toString() {
		try {
			return ow.writeValueAsString(isObject() ? object : list);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}
}
