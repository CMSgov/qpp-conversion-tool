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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Manages building an object container for JSON conversion.
 * JSON renderers can convert maps and list into JSON Strings.
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
		}, DATE {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				Type.STRING.json(value, gen);
			}
		}, INTEGER {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeNumber(Integer.parseInt(value.toObject().toString()));
				}
			}
		}, FLOAT {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeNumber(Float.parseFloat(value.toObject().toString()));
				}
			}
		}, STRING {
			public void json(JsonWrapper value, JsonGenerator gen) throws IOException {
				if (hasValue(value)) {
					gen.writeString(value.toObject().toString());
				}
			}
		}, MAP {
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
		}, LIST {
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
		}, UNKNOWN {
		};

		public boolean hasValue(JsonWrapper value) throws IOException {
			return value != null && value.toObject() != null && !value.isType(UNKNOWN);
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

		protected void jsonContainer(JsonWrapper value, JsonGenerator gen, SerializerProvider provider)
				throws IOException {
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

		protected void jsonContainer(JsonWrapper value, JsonGenerator gen, SerializerProvider provider)
				throws IOException {
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
	 * This allows for a single streaming implementation and avoids reference to Map.Entry<K,V>
	 * It is set upon put(String name, JsonWrapper value) calls to emulate an entity.
	 */
	private String keyForMapStream;

	/**
	 * Constructor for JSON container use.
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

		if (isValue()) {
			throw new UnsupportedOperationException("To use kind.VALUE, use the constructor JsonWrapper(String)");
		}

		value = null;
		childrenMap = new LinkedHashMap<>();
		childrenList = new LinkedList<>();

		// no metadata on metadata
		metadata = isMetadata() ? null : new JsonWrapper(Kind.METADATA);
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
	 * Construct a clone of the given JSON container with the option to omit the metadata.
	 * TODO might not be necessary.
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

		if (isMetadata()) {
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
	 * @return The specific type stored in this instance
	 */
	public Type getType() {
		return type;
	}

	public boolean isType(Type type) {
		return this.type == type;
	}

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

	private void put(String name, String value, Type type) {
		JsonWrapper wrapper = new JsonWrapper(value);
		wrapper.type = type;
		put(name, wrapper);
	}

	private void put(String value, Type type) {
		JsonWrapper wrapper = new JsonWrapper(value);
		wrapper.type = type;
		put(wrapper);
	}

	public JsonWrapper putDate(String name, String value) {
		try {
			put(name, validDate(value), Type.DATE);
		} catch (EncodeException e) {
			put(name, value, Type.DATE);
			throw e;
		}
		return this;
	}

	public JsonWrapper putDate(String value) {
		try {
			put(validDate(value), Type.DATE);
		} catch (EncodeException e) {
			put(value, Type.DATE);
			throw e;
		}
		return this;
	}

	public JsonWrapper putInteger(String name, String value) {
		try {
			put(name, validInteger(value), Type.INTEGER);
		} catch (EncodeException e) {
			put(name, value, Type.INTEGER);
			throw e;
		}
		return this;
	}

	public JsonWrapper putInteger(String value) {
		try {
			put(validInteger(value), Type.INTEGER);
		} catch (EncodeException e) {
			put(value, Type.INTEGER);
			throw e;
		}
		return this;
	}

	public JsonWrapper putFloat(String name, String value) {
		try {
			put(name, validFloat(value), Type.FLOAT);
		} catch (EncodeException e) {
			put(name, value, Type.FLOAT);
			throw e;
		}
		return this;
	}

	public JsonWrapper putFloat(String value) {
		try {
			put(validFloat(value), Type.FLOAT);
		} catch (EncodeException e) {
			put(value, Type.FLOAT);
			throw e;
		}
		return this;
	}

	public JsonWrapper putBoolean(String name, String value) {
		try {
			put(name, Boolean.toString(validBoolean(value)), Type.BOOLEAN);
		} catch (EncodeException e) {
			put(name, value, Type.BOOLEAN);
			throw e;
		}
		return this;
	}

	public JsonWrapper putBoolean(String value) {
		try {
			put(Boolean.toString(validBoolean(value)), Type.BOOLEAN);
		} catch (EncodeException e) {
			put(value, Type.BOOLEAN);
			throw e;
		}
		return this;
	}

	public JsonWrapper remove(String name) {
		childrenMap.remove(name);
		return this;
	}

	public String getString(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		return wrapper.value;
	}

	public Integer getInteger(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		String value = wrapper.value;
		return Integer.valueOf(value);
	}

	public Float getFloat(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		String value = wrapper.value;
		return Float.valueOf(value);
	}

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

	protected String validInteger(String value) {
		try {
			String cleanValue = FormatHelper.cleanString(value);
			Integer.parseInt(cleanValue);
			return cleanValue;
		} catch (RuntimeException e) {
			throw new EncodeException(value + " is not an integer.", e);
		}
	}

	protected String validDate(String value) {
		try {
			LocalDate thisDate = FormatHelper.formattedDateParse(value);
			return thisDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		} catch (RuntimeException e) {
			throw new EncodeException(value + " is not an date of format YYYYMMDD.", e);
		}
	}

	protected String validFloat(String value) {
		try {
			String cleanValue = FormatHelper.cleanString(value);
			Float.parseFloat(cleanValue);
			return cleanValue;
		} catch (RuntimeException e) {
			throw new EncodeException(value + " is not a number.", e);
		}
	}

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

	protected void checkMapState() {
		if (isType(Type.MAP)) {
			throw new IllegalStateException("Current state may not change (from map to list).");
		}
	}

	protected void checkListState() {
		if (isType(Type.LIST)) {
			throw new IllegalStateException("Current state may not change (from list to map).");
		}
	}

	protected boolean checkState(JsonWrapper wrapper) {
		if (wrapper == null) {
			return false;
		}

		try {
			isDuplicateEntry(wrapper);
		} catch (Exception e) {
			return false;
		}

		if (wrapper.value == null && wrapper.isKind(Kind.VALUE)) {
			return false;
		} else if (wrapper.isType(Type.UNKNOWN)) {
			return false;
		} else if (wrapper.isKind(Kind.METADATA) && this.isKind(Kind.METADATA)) {
			return wrapper.size() > 0;
		}
		return true;
	}

	public boolean isDuplicateEntry(JsonWrapper wrapper) {
		boolean duplicate = wrapper == this
				|| childrenList.contains(wrapper)
				|| childrenMap.values().contains(wrapper);
		if (duplicate) {
			throw new UnsupportedOperationException("May not add parent to itself nor a child more than once.");
		}

		return duplicate;
	}

	public boolean isMap() {
		if (isType(Type.UNKNOWN)) {
			return isKind(Kind.CONTAINER) && childrenList.isEmpty() && !childrenMap.isEmpty();
		}
		return isType(Type.MAP);
	}

	public boolean isList() {
		if (isType(Type.UNKNOWN)) {
			return isKind(Kind.CONTAINER) && !childrenList.isEmpty() && childrenMap.isEmpty();
		}
		return isType(Type.LIST);
	}

	public boolean isValue() {
		return isKind(Kind.VALUE);
	}

	public boolean isMetadata() {
		return isKind(Kind.METADATA);
	}

	public boolean hasMetadata() {
		return metadata != null && (metadata.isMap() || metadata.isList());
	}

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

	@Override
	public String toString() {
		try {
			return jsonWriter.writeValueAsString(toObject());
		} catch (JsonProcessingException e) {
			throw new EncodeException("Issue rendering JSON from JsonWrapper Map", e);
		}
	}

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
	 * @return the underlying wrapped instance: String, unmodifiable List, or unmodifiable Map
	 */
	public Object toObject() {
		if (isValue()) {
			return value;
		} else if (isList()) {
			return Collections.unmodifiableList(new LinkedList<>(childrenList));
		}
		return Collections.unmodifiableMap(new LinkedHashMap<>(childrenMap));
	}

	/**
	 * Extract wrapped content from a {@link gov.cms.qpp.conversion.encode.JsonWrapper}.
	 *
	 * @param value instance which may be wrapped
	 * @return wrapped content
	 */
	public Object stripWrapper(Object value) {
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

	public JsonWrapper getMetadata() {
		if (isMetadata()) {
			return new JsonWrapper(this);
		}
		return new JsonWrapper(metadata);
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

	public JsonWrapper putMetadata(String name, String value) {
		metadata.put(name, value);
		return this;
	}

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

	public JsonWrapper getByJsonPath(String jsonPath) {
		if (jsonPath == null) {
			return null;
		}

		String path = jsonPath.replace("$", "");
		if (path.length() == 0) {
			return this;
		} else if (path.contains(".")) {
			path = path.replaceAll("\\]", "").replaceAll("\\[", ".[")
					.substring(1);
		} else {
			path = path.replaceAll("'\\]", ".").replaceAll("\\['", "")
					.replaceAll("\\]", ".");
			path = path.substring(0, path.length() - 1);
		}

		List<String> paths = new LinkedList<>();
		for (String entry : path.split("\\.")) {
			paths.add(entry);
		}
		return getByJsonPath(paths);
	}

	public JsonWrapper get(JsonPath jsonPath) {
		if (jsonPath == null) {
			return null;
		}
		if (!jsonPath.isDefinite()) {
			throw new UnsupportedOperationException("Only definite paths are supported at this time.");
		}
		return getByJsonPath(jsonPath.getPath());
	}

	public JsonWrapper get(String name) {
		JsonWrapper wrapper = childrenMap.get(name);
		return wrapper == null ? null : new JsonWrapper(wrapper);
	}

	public JsonWrapper get(int index) {
		if (index >= 0 && index < childrenList.size()) {
			return new JsonWrapper(childrenList.get(index));
		}
		return null;
	}
}
