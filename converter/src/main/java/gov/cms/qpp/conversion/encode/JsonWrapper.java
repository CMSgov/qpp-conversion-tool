package gov.cms.qpp.conversion.encode;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.jayway.jsonpath.JsonPath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.util.CloneHelper;
import gov.cms.qpp.conversion.util.FormatHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Manages building an object container for JSON conversion.
 * JSON renderers can convert maps and lists into JSON Strings.
 * This class is a wrapper around a container, value, and
 * metadata Kind with container types, list and map.
 */
public class JsonWrapper implements Serializable {

	public static enum Kind {
		CONTAINER, VALUE, METADATA;
	}

	public static enum Type {
		BOOLEAN {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeBoolean(Boolean.parseBoolean(value.toObject().toString()));
				}
			}
		},
		DATE {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				Type.STRING.json(value, gen);
			}
		},
		INTEGER {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeNumber(Integer.parseInt(value.toObject().toString()));
				}
			}
		},
		FLOAT {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeNumber(Float.parseFloat(value.toObject().toString()));
				}
			}
		},
		STRING {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeString(value.toObject().toString());
				}
			}
		},
		MAP {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeObject(value.toObject());
				}
			}

			public void metadata(JsonWrapper value, JsonGenerator gen) throws IOException {
				gen.writeStartObject();
				if (value.hasMetadata()) {
					gen.writeObjectField(METADATA_HOLDER, value.getMetadata());
				}
				value.stream().forEach(entry -> {
					try {
						gen.writeObjectField(entry.getKey(), entry);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
				gen.writeEndObject();
			}
		},
		LIST {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeObject(value.toObject());
				}
			}

			public void metadata(JsonWrapper value, JsonGenerator gen) throws IOException {
				gen.writeStartArray();
				if (value.hasMetadata()) {
					gen.writeObject(value.getMetadata());
				}
				value.stream().forEach(entry -> {
					try {
						gen.writeObject(entry);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
				gen.writeEndArray();
			}
		},
		UNKNOWN {
		};

		public boolean hasValue(JsonWrapper value) throws IOException {
			return null != value && null != value.toObject() && !value.isType(UNKNOWN);
		}

		public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
		}

		public void metadata(JsonWrapper value, JsonGenerator gen) throws IOException {
		}
	}

	public static final String METADATA_HOLDER = "metadata_holder";
	public static final String ENCODING_KEY = "encodeLabel";
	public static final ObjectMapper jsonMapper;
	private static final ObjectMapper metaMapper;

	private static DefaultPrettyPrinter standardPrinter() {
		DefaultIndenter withLinefeed = new DefaultIndenter("  ", "\n");
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(withLinefeed);
		return printer;
	}

	/**
	 * Custom JsonWrapper serialization logic to handle the metadata and type handling.
	 * This default impl ignores metadata.
	 */
	static class JsonWrapperSerilizer extends StdSerializer<JsonWrapper> {

		protected JsonWrapperSerilizer() {
			super(JsonWrapper.class);
		}

		protected void jsonContainer(JsonWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			value.getType().json(value, gen);
		}

		@Override
		public void serialize(JsonWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (value.isKind(Kind.CONTAINER)) {
				jsonContainer(value, gen, provider);
			} else {
				value.getType().json(value, gen);
			}
		}
	}

	/**
	 * Custom JsonWrapper serialization logic to handle the metadata and type handling.
	 * This subclass also processes the metadata.
	 */
	static class JsonWrapperMetadataSerilizer extends JsonWrapperSerilizer {

		protected void jsonContainer(JsonWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (value.hasMetadata()) {
				value.getType().metadata(value, gen);
			} else {
				super.jsonContainer(value, gen, provider);
			}
		}
	}

	/**
	 * Initialize the serializers
	 */
	static {
		jsonMapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addSerializer(JsonWrapper.class, new JsonWrapperSerilizer());
		jsonMapper.registerModule(module);

		metaMapper = new ObjectMapper();
		module = new SimpleModule();
		module.addSerializer(JsonWrapper.class, new JsonWrapperMetadataSerilizer());
		metaMapper.registerModule(module);
	}

	static ObjectWriter standardWriter() {
		return jsonMapper.writer().with(standardPrinter());
	}

	static ObjectWriter metadataWriter() {
		return metaMapper.writer().with(standardPrinter());
	}

	private static final ObjectWriter jsonWriter = standardWriter();
	private static final ObjectWriter withMetadataWriter = metadataWriter();

	private final String value;
	private final Map<String, JsonWrapper> childrenMap;
	private final List<JsonWrapper> childrenList;
	private final JsonWrapper metadata;
	private final Kind kind;
	private Type type = Type.UNKNOWN;

	/**
	 * This is the key on the JsonWrapper that was used to store it in the parent wrapper.
	 * This allows for a single streaming implementation and avoids reference to {@code Map.Entry<K,V>}
	 * It is set upon put(String name, JsonWrapper value) calls to emulate an entity.
	 */
	private String keyForMapStream;

	/**
	 * Constructor for Json container use.
	 */
	public JsonWrapper() {
		this(Kind.CONTAINER);
	}

	/**
	 * Construct a JSON container for a given kind.
	 *
	 * @param kind
	 */
	public JsonWrapper(Kind kind) {
		this.kind = kind;

		if (kind == Kind.VALUE) {
			throw new UnsupportedOperationException("To use kind.VALUE, use the constructor JsonWrapper(String)");
		}

		value = null;
		childrenMap = new LinkedHashMap<>();
		childrenList = new LinkedList<>();

		// no metadata on metadata
		if (kind == Kind.METADATA) {
			this.metadata = null;
		} else {
			this.metadata = new JsonWrapper(Kind.METADATA);
		}
	}

	/**
	 * Construct a JSON container for a string value.
	 * A string value is a leaf node.
	 *
	 * @param value
	 */
	public JsonWrapper(String value) {
		kind = Kind.VALUE;
		type = Type.STRING;
		this.value = value;
		childrenMap = null;
		childrenList = null;
		metadata = null;
	}

	public JsonWrapper(Boolean value) {
		this(value.toString());
		type = Type.BOOLEAN;
	}

	public JsonWrapper(Integer value) {
		this(value.toString());
		type = Type.INTEGER;
	}

	public JsonWrapper(Float value) {
		this(value.toString());
		type = Type.FLOAT;
	}

	/**
	 * Construct a clone of the given JSON container.
	 *
	 * @param wrapper
	 */
	public JsonWrapper(JsonWrapper wrapper) {
		this(wrapper, true);
	}

	/**
	 * Construct a clone of the given JSON container
	 * with the option to omit the metadata.
	 *
	 * @param wrapper
	 * @param withMetadata
	 */
	private JsonWrapper(JsonWrapper wrapper, boolean withMetadata) {
		kind = wrapper.kind;
		type = wrapper.type;
		value = wrapper.value;

		childrenMap = CloneHelper.deepClone(wrapper.childrenMap);
		childrenList = CloneHelper.deepClone(wrapper.childrenList);

		if (this.kind == Kind.METADATA) {
			metadata = null;
		} else if (withMetadata) {
			metadata = CloneHelper.deepClone(wrapper.metadata);
		} else {
			// instance allows for new metadata to be added
			metadata = new JsonWrapper(Kind.METADATA);
		}
	}

	/**
	 * Used for casting to a type on value get actions.
	 *
	 * @return The specific type stored in this instance.
	 */
	public Type getType() {
		return type;
	}

	public boolean isType(Type type) {
		return this.type == type;
	}

	/**
	 * (package-private) helper to change this wrapper’s Type and return self for chaining.
	 */
	JsonWrapper setType(Type type) {
		this.type = type;
		return this;
	}

	/**
	 * Used for collection determinations during stream (and other) actions.
	 *
	 * @return the general type of use for this instance
	 */
	public Kind getKind() {
		return kind;
	}

	public boolean isKind(Kind kind) {
		return this.kind == kind;
	}

	/**
	 * @return The name used to store this entry in the parent.
	 */
	public String getKey() {
		return keyForMapStream;
	}

	public JsonWrapper copyWithoutMetadata() {
		return new JsonWrapper(this, false);
	}

	/**
	 * removes all data from the map, list, and metadata collections.
	 *
	 * @return chaining self ref
	 */
	public JsonWrapper clear() {
		if (!isValue()) {
			childrenMap.clear();
			childrenList.clear();
			// metadata do not have metadata but are clearable wrappers.
			if (metadata != null) {
				metadata.clear();
			}
			setType(Type.UNKNOWN);
		}
		return this;
	}

	/**
	 * Places a named String within a map container wrapper.
	 *
	 * @param name  key for value
	 * @param value keyed value
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String name, String value) {
		put(name, value, Type.STRING);
		return this;
	}

	/**
	 * Places a named Integer within the map container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String name, Integer value) {
		put(name, Integer.toString(value), Type.INTEGER);
		return this;
	}

	/**
	 * Places a named Float within the map container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String name, Float value) {
		put(name, Float.toString(value), Type.FLOAT);
		return this;
	}

	/**
	 * Places a named Boolean within the map container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String name, Boolean value) {
		put(name, Boolean.toString(value), Type.BOOLEAN);
		return this;
	}

	/**
	 * Places an unnamed String within the list container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String value) {
		put(value, Type.STRING);
		return this;
	}

	/**
	 * Places an unnamed Integer within the list container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(Integer value) {
		put(value.toString(), Type.INTEGER);
		return this;
	}

	/**
	 * Places an unnamed Float within the list container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(Float value) {
		put(value.toString(), Type.FLOAT);
		return this;
	}

	/**
	 * Places an unnamed Boolean within the list container wrapper.
	 *
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(Boolean value) {
		put(value.toString(), Type.BOOLEAN);
		return this;
	}

	/**
	 * Places an unnamed child wrapper within the wrapper.
	 * Think of this as adding a JSON array entry.
	 *
	 * @param value item to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(JsonWrapper value) {
		checkMapState();
		if (checkState(value)) {
			childrenList.add(value);
			type = Type.LIST;
		}
		return this;
	}

	/**
	 * The master putter of MAP elements. They must specify the type.
	 * The JSON values are all represented as strings because JSON is a string.
	 * The type parameter is a sort of metadata about the entry for the
	 * wrapper to know what data was stored for toString processing,
	 * format validation, and value fetching.
	 *
	 * @param name
	 * @param value
	 * @param type
	 */
	private void put(String name, String value, Type type) {
		JsonWrapper wrapper = new JsonWrapper(value);
		wrapper.type = type;
		put(name, wrapper);
	}

	/**
	 * The master putter of LIST elements. They must specify the type.
	 * The JSON values are all represented as strings because JSON is a string.
	 * The type parameter is a sort of metadata about the entry for the
	 * wrapper to know what data was stored for toString processing,
	 * format validation, and value fetching.
	 *
	 * @param value
	 * @param type
	 */
	private void put(String value, Type type) {
		JsonWrapper wrapper = new JsonWrapper(value);
		wrapper.type = type;
		put(wrapper);
	}

	/**
	 * Places a named child wrapper within the wrapper.
	 * Think of this as adding an attribute to a JSON hash.
	 *
	 * @param name  key for value
	 * @param value keyed value
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String name, JsonWrapper value) {
		checkListState();
		if (checkState(value)) {
			value.keyForMapStream = name;
			childrenMap.put(name, value);
			type = Type.MAP;
		}
		return this;
	}

	/**
	 * Places a named String that represents a date within the wrapper.
	 *
	 * @param name  key for value
	 * @param value keyed value which must conform with {@link #validDate(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putDate(String name, String value) {
		try {
			put(name, validDate(value), Type.DATE);
		} catch (EncodeException e) {
			put(name, value, Type.DATE);
			throw e;
		}
		return this;
	}

	/**
	 * Places an unnamed String that represents a date within the wrapper.
	 *
	 * @param value that must conform with {@link #validDate(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putDate(String value) { // TODO only used in unit tests
		try {
			put(validDate(value), Type.DATE);
		} catch (EncodeException e) {
			put(value, Type.DATE);
			throw e;
		}
		return this;
	}

	/**
	 * Places a named String that represents an {@link java.lang.Integer} within the wrapper.
	 *
	 * @param name  key for value
	 * @param value keyed value which must conform with {@link #validInteger(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putInteger(String name, String value) {
		try {
			put(name, validInteger(value), Type.INTEGER);
		} catch (EncodeException e) {
			put(name, value, Type.INTEGER);
			throw e;
		}
		return this;
	}

	/**
	 * Places an unnamed String that represents an {@link java.lang.Integer} within the wrapper.
	 *
	 * @param value {@link String} must conform with {@link #validInteger(String)} validation
	 * @return {@link JsonWrapper}
	 */
	public JsonWrapper putInteger(String value) { // TODO only used in unit tests
		try {
			put(validInteger(value), Type.INTEGER);
		} catch (EncodeException e) {
			put(value, Type.INTEGER);
			throw e;
		}
		return this;
	}

	/**
	 * Places a named String that represents a {@link java.lang.Float} within the wrapper.
	 *
	 * @param name  key for value
	 * @param value keyed value that must conform with {@link #validFloat(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putFloat(String name, String value) { // TODO only used in unit tests
		try {
			put(name, validFloat(value), Type.FLOAT);
		} catch (EncodeException e) {
			put(name, value, Type.FLOAT);
			throw e;
		}
		return this;
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Float} within the wrapper.
	 *
	 * @param value that must conform with {@link #validFloat(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putFloat(String value) { // TODO only used in unit tests
		try {
			put(validFloat(value), Type.FLOAT);
		} catch (EncodeException e) {
			put(value, Type.FLOAT);
			throw e;
		}
		return this;
	}

	/**
	 * Places a named String that represents a {@link java.lang.Boolean} within the wrapper.
	 *
	 * @param name  key for value
	 * @param value keyed value that must conform with {@link #validBoolean(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putBoolean(String name, String value) {
		try {
			put(name, Boolean.toString(validBoolean(value)), Type.BOOLEAN);
		} catch (EncodeException e) {
			put(name, value, Type.BOOLEAN);
			throw e;
		}
		return this;
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Boolean} within the wrapper.
	 *
	 * @param value that must conform with {@link #validBoolean(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putBoolean(String value) { // TODO only used in unit tests
		try {
			put(Boolean.toString(validBoolean(value)), Type.BOOLEAN);
		} catch (EncodeException e) {
			put(value, Type.BOOLEAN);
			throw e;
		}
		return this;
	}

	/**
	 * Removes the named key and value from the children map
	 *
	 * @param name key that needs to be removed
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper remove(String name) {
		childrenMap.remove(name);
		return this;
	}

	/**
	 * Retrieve a named {@link String} from the {@link JsonWrapper}.
	 *
	 * @see #get(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public String getString(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		return wrapper.value;
	}

	/**
	 * Retrieve a named {@link Integer} from the {@link JsonWrapper}.
	 *
	 * @see #get(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public Integer getInteger(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		String value = wrapper.value;
		return Integer.valueOf(value);
	}

	/**
	 * Retrieve a named {@link Float} from the {@link JsonWrapper}.
	 *
	 * @see #get(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	public Float getFloat(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		String value = wrapper.value;
		return Float.valueOf(value);
	}

	/**
	 * Retrieve a named {@link Boolean} from the {@link JsonWrapper}.
	 *
	 * @see #get(String)
	 * @param name key for value
	 * @return retrieved keyed value
	 */
	@SuppressFBWarnings(value = "NP_BOOLEAN_RETURN_NULL",
			justification = "Null is desired when not found.")
	public Boolean getBoolean(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		String value = wrapper.value;
		return Boolean.valueOf(value);
	}

	/**
	 * Validates that the given value is a parsable integer.
	 *
	 * @param value to validate
	 * @return valid Integer
	 * @throws EncodeException
	 */
	protected String validInteger(String value) {
		try {
			String cleanValue = FormatHelper.cleanString(value);
			Integer.parseInt(cleanValue);
			return cleanValue;
		} catch (RuntimeException e) {
			throw new EncodeException(value + " is not an integer.", e);
		}
	}

	/**
	 * Validates that the given value conforms to an ISO date with or without separators.
	 * It can include a time but is unnecessary.
	 *
	 * @param value to validate
	 * @return valid date String
	 * @throws EncodeException
	 */
	protected String validDate(String value) {
		try {
			LocalDate thisDate = FormatHelper.formattedDateParse(value);
			return thisDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (RuntimeException e) {
			throw new EncodeException(value + " is not an date of format YYYYMMDD.", e);
		}
	}

	/**
	 * Validates that the given value is a parsable numeric value.
	 *
	 * @param value to validate
	 * @return valid Float value
	 * @throws EncodeException
	 */
	protected String validFloat(String value) {
		try {
			String cleanValue = FormatHelper.cleanString(value);
			Float.parseFloat(cleanValue);
			return cleanValue;
		} catch (RuntimeException e) {
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
		String cleanValue = FormatHelper.cleanString(value);

		if ("true".equals(cleanValue) || "yes".equals(cleanValue) || "y".equals(cleanValue)) {
			return true;
		}
		if ("false".equals(cleanValue) || "no".equals(cleanValue) || "n".equals(cleanValue)) {
			return false;
		}

		throw new EncodeException(cleanValue + " is not a boolean.");
	}

	/**
	 * Helps enforce the initialized representation of the {@link JsonWrapper} as a hash or an array.
	 */
	protected void checkMapState() {
		if (isType(Type.MAP)) {
			throw new IllegalStateException("Current state may not change (from map to list).");
		}
	}

	/**
	 * Helps enforce the initialized representation of the {@link JsonWrapper} as a hash or an array.
	 */
	protected void checkListState() {
		if (isType(Type.LIST)) {
			throw new IllegalStateException("Current state may not change (from list to map).");
		}
	}

	/**
	 * Helps enforce no null or empty values added to a {@link JsonWrapper}.
	 *
	 * @param wrapper should be null
	 */
	protected boolean checkState(JsonWrapper wrapper) {
		if (wrapper == null) { // no null entries
			return false;
		}

		try {
			isDuplicateEntry(wrapper);  // no self references
		} catch (Exception e) {
			return false;
		}

		if (wrapper.value == null && wrapper.isKind(Kind.VALUE)) { // no null values
			return false;
		} else if (wrapper.isType(Type.UNKNOWN)) { // no empty containers
			return false;
		} else if (wrapper.isKind(Kind.METADATA) && this.isKind(Kind.METADATA)) { // allow metadata mergers
			return wrapper.size() > 0;
		}
		return true; // must be good if we made it through the gauntlet
	}

	/**
	 * Helps prevent duplicate entries. It is not sure why duplicates are added.
	 * The decoders seem to try and this prevents it without without changing decoder code.
	 * This does not check higher level parents and deeper children.
	 * TODO it could recursively check children.
	 *
	 * @param wrapper
	 * @return
	 */
	public boolean isDuplicateEntry(JsonWrapper wrapper) {
		boolean duplicate = wrapper == this
				|| childrenList.contains(wrapper)
				|| childrenMap.values().contains(wrapper);
		if (duplicate) {
			throw new UnsupportedOperationException("May not add parent to itself nor a child more than once.");
		}

		return duplicate;
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content is a JSON hash.
	 *
	 * @return boolean is this a JSON object or hash
	 */
	public boolean isMap() {
		if (isType(Type.UNKNOWN)) {
			return isKind(Kind.CONTAINER) && childrenList.isEmpty() && !childrenMap.isEmpty();
		}
		return isType(Type.MAP);
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content is a JSON array.
	 *
	 * @return boolean is this a JSON array
	 */
	public boolean isList() {
		if (isType(Type.UNKNOWN)) {
			return isKind(Kind.CONTAINER) && !childrenList.isEmpty() && childrenMap.isEmpty();
		}
		return isType(Type.LIST);
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content is a JSON value (leaf).
	 *
	 * @return boolean is this a JSON value
	 */
	public boolean isValue() {
		return isKind(Kind.VALUE);
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content IS metadata.
	 *
	 * @return boolean is this metadata
	 */
	public boolean isMetadata() {
		return isKind(Kind.METADATA);
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content HAS metadata.
	 *
	 * @return boolean true if this has metadata.
	 */
	public boolean hasMetadata() {
		return metadata != null && (metadata.isMap() || metadata.isList());
	}

	/**
	 * Stream of wrapped container or single value.
	 *
	 * @return Stream of wrapped data.
	 */
	public Stream<JsonWrapper> stream() {
		Stream<JsonWrapper> stream;

		if (isValue() && value != null) {
			stream = Stream.of(this);
		} else if (isList()) {
			stream = childrenList.stream();
		} else {
			stream = childrenMap.entrySet()
					.stream()
					.map(entry -> entry.getValue());
		}
		return stream;
	}

	/**
	 * Valid JSON String representation of the {@link JsonWrapper}.
	 *
	 * @return JSON
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")  // toObject() returns internal List or Map
	@Override
	public String toString() {
		try {
			return jsonWriter.writeValueAsString(toObject());
		} catch (JsonProcessingException e) {
			throw new EncodeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}

	/**
	 * Valid JSON String representation of the {@link JsonWrapper}
	 * with its metadata in metadata_holder hash key.
	 *
	 * @return JSON with metadata
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")  // writeValueAsString may expose internal structures
	public String toStringWithMetadata() {
		try {
			return withMetadataWriter.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new EncodeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}

	/**
	 * It returns a Java Object class of the wrapped implementation.
	 * If the instance is a leaf entity then the String value will be returned,
	 * else if the list has entries then a List implementation will be returned,
	 * finally, the Map instance is returned even if empty.
	 *
	 * It is used in the JSON generation process to obtain the underlying impl.
	 *
	 * @return the underlying wrapped instance: String, Map, or List
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")  // toObject() returns direct childrenList or childrenMap
	public Object toObject() {
		if (isValue()) {
			return value;
		} else if (isList()) {
			return childrenList;
		}
		return childrenMap;
	}

	/**
	 * Extract wrapped content from a {@link gov.cms.qpp.conversion.encode.JsonWrapper}.
	 *
	 * @param value instance which may be wrapped
	 * @return wrapped content
	 */
	public Object stripWrapper(Object value) { // TODO only used in unit tests
		Object internalValue = value;
		if (value instanceof JsonWrapper) {
			JsonWrapper wrapper = (JsonWrapper) value;
			internalValue = wrapper.toObject();
		}
		return internalValue;
	}

	/**
	 * Convenience method to get the JsonWrapper's JSON content as an input stream.
	 *
	 * @return input stream containing serialized JSON
	 */
	public Source toSource() {
		byte[] qppBytes = toString().getBytes(StandardCharsets.UTF_8);
		return new InputStreamSupplierSource("QPP", new ByteArrayInputStream(qppBytes));
	}

	// TODO it feels like attachMetadata methods should be outside this class
	void attachMetadata(Node node) {
		attachMetadata(node, "");
	}

	void attachMetadata(Node node, String encodeLabel) {
		JsonWrapper metadata = new JsonWrapper(Kind.METADATA);

		metadata.put(ENCODING_KEY, encodeLabel)
				.put("nsuri", node.getDefaultNsUri())
				.put("template", node.getType().name())
				.put("path", node.getOrComputePath());

		if (node.getLine() != Node.DEFAULT_LOCATION_NUMBER) {
			metadata.put("line", String.valueOf(node.getLine()));
		}
		if (node.getColumn() != Node.DEFAULT_LOCATION_NUMBER) {
			metadata.put("column", String.valueOf(node.getColumn()));
		}
		addMetadata(metadata);
	}

	/**
	 * Returns the metadata for the wrapper instance unless it is metadata itself.
	 * If it is metadata itself then it returns self.
	 * Currently, there is no metadata on metadata.
	 * @return the metadata on the instance or self if self is metadata.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")  // getMetadata() returns direct internal metadata object
	public JsonWrapper getMetadata() {
		if (isMetadata()) {
			return this;
		}
		return metadata;
	}

	void mergeMetadata(JsonWrapper otherWrapper, String encodeLabel) {
		JsonWrapper metadata = otherWrapper.getMetadata();
		if (metadata.isList()) {
			metadata.stream().forEach(other -> {
				other.put(ENCODING_KEY, encodeLabel);
				addMetadata(other);
			});
		} else {
			metadata.put(ENCODING_KEY, encodeLabel);
			addMetadata(metadata);
		}
	}

	public JsonWrapper addMetadata(JsonWrapper newMetadata) {
		if (newMetadata != null) {
			JsonWrapper localMetadata = getMetadata();
			if (localMetadata.isMap()) {
				JsonWrapper wrappedMetadata = new JsonWrapper(localMetadata);
				localMetadata.clear();
				localMetadata.put(wrappedMetadata);
			}
			localMetadata.put(newMetadata.getMetadata());
		}
		return this;
	}

	/**
	 * add a metadata key pair to the current JsonWrapper
	 * @param name  the metadata name
	 * @param value the metadata value
	 */
	public JsonWrapper putMetadata(String name, String value) {
		metadata.put(name, value);
		return this;
	}

	/**
	 * @return the count of entries in the wrapper
	 */
	public int size() {
		if (isType(Type.UNKNOWN)) {
			return 0;
		}
		if (isValue()) {
			return (value == null) ? 0 : 1;
		}
		if (isList()) {
			return childrenList.size();
		}
		return childrenMap.size();
	}

	JsonWrapper getByJsonPath(List<String> jsonPath) {
		JsonWrapper pathWrapper = this;
		for (String path : jsonPath) {
			if (path.startsWith("[")) {
				path = path.substring(1);
				int index = Integer.parseInt(path);
				pathWrapper = pathWrapper.get(index);
			} else {
				pathWrapper = pathWrapper.get(path);
			}
			if (pathWrapper == null) {
				break;
			}
		}
		return pathWrapper;
	}

	/**
	 * Accepts a bracket or dot path notation to locate a wrapper child.
	 * It does not directly locate the node.
	 * It tokenizes the path into a list of strings.
	 * Currently it only works on direct path and not wildcard/function paths.
	 *
	 * @param jsonPath JSON path notation
	 * @return wrapper containing the data of the given path or null if not found
	 */
	public JsonWrapper getByJsonPath(String jsonPath) {
		if (jsonPath == null) {
			return null; // could return new empty instance of JsonWrapper for null protection
		}

		String path = jsonPath.replace("$", "");
		if (path.length() == 0) {
			return this;
		} else if (path.contains(".")) {
			// conform dot containing paths to the results of bracket paths below
			path = path
					.replaceAll("\\]", "")
					.replaceAll("\\[", ".[")
					.substring(1);
		} else {
			// remove most of the brackets while retaining an index bracket for easy identification
			path = path
					.replaceAll("'\\]", ".")
					.replaceAll("\\['", "")
					.replaceAll("\\]", ".");
			path = path.substring(0, path.length() - 1);
		}
		// note that array entries keep the leading '[' for detection in the list method signature

		List<String> paths = new LinkedList<>();
		for (String entry : path.split("\\.")) {
			paths.add(entry);
		}
		return getByJsonPath(paths);
	}

	/**
	 * Accepts the compiled JsonPath to locate a wrapper child.
	 * This checks for null and definite status before calling the string parser instance.
	 * It is called get to be in line with the get for map and list that return wrappers.
	 *
	 * @param jsonPath
	 * @return wrapper
	 */
	public JsonWrapper get(JsonPath jsonPath) {
		if (jsonPath == null) {
			return null;
		}
		if (!jsonPath.isDefinite()) {
			throw new UnsupportedOperationException("Only definite paths are supported at this time.");
		}
		return getByJsonPath(jsonPath.getPath());
	}

	/**
	 * Return the named value from the {@link JsonWrapper}.
	 * Think of this as retrieval of a JSON hash attribute
	 *
	 * @param name key for value
	 * @return T retrieved keyed value
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	public JsonWrapper get(String name) {
		return childrenMap.get(name);
	}

	/**
	 * Get a List element by index.
	 *
	 * @param index integer element number starting with zero
	 * @return the wrapper at given index or null
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP")
	public JsonWrapper get(int index) {
		if (index >= 0 && index < childrenList.size()) {
			return childrenList.get(index);
		}
		return null;
	}
}
