package gov.cms.qpp.conversion.encode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.util.CloneHelper;
import gov.cms.qpp.conversion.util.FormatHelper;

/**
 * Manages building a "simple" object of JSON conversion.
 * JSON renderers can convert maps and list into JSON Strings.
 * This class is a wrapper around a list/map impl.
 */
public class JsonWrapper {

	public static enum Kind {
		VALUE, OBJECT, METADATA; // TODO asdf clear up object and map refs
	}
	public static enum Type {
		BOOLEAN, DATE, INTEGER, FLOAT, STRING, MAP, LIST, UNKNOWN;
	}
	
	public static final String METADATA_HOLDER = "metadata_holder";
	public static final String ENCODING_KEY = "encodeLabel";
	private static ObjectMapper jsonMapper;
	private static ObjectMapper metaMapper;

	private static void stripMetadata(JsonWrapper wrapper) { // TODO asdf check that this is needed (unused?)
		if (wrapper == null) {
			return;
		}
		wrapper.metadata.clear();
		wrapper.childrenMap.values().stream().forEach(JsonWrapper::stripMetadata);
		wrapper.childrenList.stream().forEach(JsonWrapper::stripMetadata);
	}

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
	private static class JsonWrapperSerilizer extends StdSerializer<JsonWrapper> {

		protected JsonWrapperSerilizer() {
			super(JsonWrapper.class);
		}
		
		protected void objectHandling(JsonWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			Object wrappedEntity = value.toObject();
			gen.writeObject(wrappedEntity);
		}

		@Override
		public void serialize(JsonWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			Object wrappedEntity = value.toObject();
			switch (value.type) {
				case BOOLEAN: gen.writeBoolean( Boolean.parseBoolean(wrappedEntity.toString()));
					break;
				case INTEGER: gen.writeNumber( Integer.parseInt(wrappedEntity.toString()));
					break;
				case FLOAT:   gen.writeNumber( Float.parseFloat(wrappedEntity.toString()));
					break;
				case DATE:
				case STRING:  gen.writeString( wrappedEntity.toString() );
					break;
				default: 	  objectHandling(value, gen, provider); // Type.UNKNOWN, MAP, LIST
					break;
			}
		}
	}
	/**
	 * Custom JsonWrapper serialization logic to handle the metadata and type handling. 
	 * This subclass also processes the metadata.
	 */
	private static class JsonWrapperMetadataSerilizer extends JsonWrapperSerilizer {
		
		protected void objectHandling(JsonWrapper value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			if (value.isType(Type.MAP)) {
				if (value.hasMetadata()) {
					gen.writeStartObject();
					if (value.hasMetadata()) {
						gen.writeObjectField(METADATA_HOLDER, value.getMetadata());
					}
					value.stream().forEach( entry -> {
						try {
							gen.writeObjectField(entry.getKey(), entry);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					});
					gen.writeEndObject();
				} else {
					super.objectHandling(value, gen, provider);
				}
			} else if (value.isType(Type.LIST)) {
				
				gen.writeStartArray();
				if (value.hasMetadata()) {
					gen.writeObject(value.getMetadata());
				}
	            value.stream().forEach( entry ->{
	                try {
						gen.writeObject(entry);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
	            });
	            gen.writeEndArray();
	        } else if (value.hasMetadata()) {
				super.objectHandling(value.getMetadata(), gen, provider);
	        }
		} 
	}
	
	/**
	 * Initialize the serializes
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
	private final Map<String, JsonWrapper> childrenMap; // TODO asdf was object
	private final List<JsonWrapper> childrenList; // TODO asdf was list
	private final JsonWrapper metadata;
	private final Kind kind;
	private Type type = Type.UNKNOWN;
	
	/**
	 * This is the key on the JsonWrapper that was used to store it in the parent wrapper.
	 * This allows for a single streaming implementation and avoids reference to Map.Entity<K,V>
	 * 
	 * It is set upon put(String name, JsonWrapper value) calls to emulate an entity.
	 */
	private String keyForMapStream; // TODO maybe refactor name or concept.
	

	/**
	 * Constructor for Json Object and List use. 
	 */
	public JsonWrapper() {
		this(Kind.OBJECT);
	}
	/**
	 * Cunstruct a JSON container for a given kind.
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
		metadata = isMetadata() ?null :new JsonWrapper(Kind.METADATA);
	}
	/**
	 * Construct a JSON container for a string value.
	 * A string value is a leaf node.
	 * 
	 * @param value
	 */
	public JsonWrapper(String value) {
		kind = Kind.VALUE;
		this.value = value;
		childrenMap = null;
		childrenList = null;
		metadata = null;
	}
	/**
	 * Construct a clone of the given JSON container.
	 * @param wrapper
	 */
	public JsonWrapper(JsonWrapper wrapper) {
		this(wrapper, true);
	}
	/**
	 * Construct a clone of the given JSON container 
	 * with the option to omit the metadata. TODO might not be necessary.
	 * @param wrapper
	 * @param withMetadata
	 */
	private JsonWrapper(JsonWrapper wrapper, boolean withMetadata) {
		kind = wrapper.kind;
		value = wrapper.value;
		
		childrenMap = CloneHelper.deepClone(wrapper.childrenMap);
		childrenList = CloneHelper.deepClone(wrapper.childrenList);
		
		if (withMetadata) {
			metadata = CloneHelper.deepClone(wrapper.metadata);
		} else {
			// instance allows for new metadata to be added
			metadata = new JsonWrapper(Kind.METADATA);
		}	
	}

	/**
	 * Used for casting to a type on value get actions.
	 * @return The specific type stored in this instance,
	 */
	public Type getType() {
		return type;
	}
	public boolean isType(Type type) {
		return this.type == type;
	}
	
	/**
	 * Used for collection determinations during stream (and other) actions.
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
	 * Extract wrapped content from a {@link gov.cms.qpp.conversion.encode.JsonWrapper}.
	 *
	 * @param value {@link Object} which may be wrapped
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
	 * removes all data from the map, list, and metadata collections.
	 * @return chaining self ref
	 */
	public JsonWrapper clear() {
		if ( ! isValue() ) {
			childrenMap.clear();
			childrenList.clear();
			metadata.clear();
		}
		return this;
	}
	
	/**
	 * Places a named String within the wrapper. See {@link #putObject(String, Object)}
	 *
	 * @param name key for value
	 * @param value keyed value
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String name, String value) {
		put(name, value, Type.STRING);
		return this;
	}

	/**
	 * Places an unnamed String within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(String value) {
		put(value, Type.STRING);
		return this;
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
	 * @see #putObject(Object)
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
	 * @see #putObject(String, Object)
	 * @param name key for value
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
	public JsonWrapper put(String name, Integer value) {
		put(name, Integer.toString(value), Type.INTEGER);
		return this;
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Integer} within the wrapper.
	 *
	 * @see #putObject(Object)
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
	public JsonWrapper put(Integer value) {
		put(value.toString(), Type.INTEGER);
		return this;
	}

	/**
	 * Places an named String that represents a {@link java.lang.Float} within the wrapper.
	 *
	 * @see #putObject(String, Object)
	 * @param name key for value
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
	public JsonWrapper put(String name, Float value) {
		put(name, Float.toString(value), Type.FLOAT);
		return this;
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Float} within the wrapper.
	 *
	 * @see #putObject(Object)
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
	public JsonWrapper putFloat(Float value) {
		put(value.toString(), Type.FLOAT);
		return this;
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
			put(name, Boolean.toString(validBoolean(value)), Type.BOOLEAN);
		} catch (EncodeException e) {
			put(name, value, Type.BOOLEAN);
			throw e;
		}
		return this;
	}
	public JsonWrapper put(String name, boolean value) {
		put(name, Boolean.toString(value), Type.BOOLEAN);
		return this;
	}

	/**
	 * Places an unnamed String that represents a {@link java.lang.Boolean} within the wrapper.
	 *
	 * @see #putObject(Object)
	 * @param value that must conform with {@link #validBoolean(String)} validation
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper putBoolean(String value) { // TODO only used in unit tests
		try {
			put( Boolean.toString(validBoolean(value)), Type.BOOLEAN);
		} catch (EncodeException e) {
			put(value, Type.BOOLEAN);
			throw e;
		}
		return this;
	}
	public JsonWrapper put(Boolean value) {
		put(value.toString(), Type.BOOLEAN);
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
	 * Places an unnamed {@link java.lang.Object} within the wrapper. In the event the named object is
	 * also a {@link gov.cms.qpp.conversion.encode.JsonWrapper} its wrapped content will be extracted.
	 *
	 * Think of this as adding a JSON array entry.
	 *
	 * @param value object to place in wrapper
	 * @return <i><b>this</b></i> reference for chaining
	 */
	public JsonWrapper put(JsonWrapper value) { // TODO asdf maybe refactor to add(JsW)?
		checkMapState();
		if (checkState(value)) {
			childrenList.add(value);
			type = Type.LIST;
		}
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
	public Boolean getBoolean(String name) {
		JsonWrapper wrapper = get(name);
		if (wrapper == null) {
			return null;
		}
		String value = wrapper.value;
		return Boolean.valueOf(value);
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
	public JsonWrapper get(String name) {
		return childrenMap.get(name);
	}
	public JsonWrapper get(int index) {
		if (index >= 0 && index < childrenList.size()) {
			return childrenList.get(index);
		}
		return null;
	}

	/**
	 * Validates that the given value is an parsable integer.
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
	 * Validates that the given value is an parsable numeric value.
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
	 *
	 * @param check should be null
	 */
	protected void checkMapState() {
		if (isType(Type.MAP)) {
			throw new IllegalStateException("Current state may not change (from object to list).");
		}
	}
	protected void checkListState() {
		if (isType(Type.LIST)) {
			throw new IllegalStateException("Current state may not change (from list to object).");
		}
	}
	protected boolean checkState(JsonWrapper wrapper) { // TODO asdf exhaustive unit test coverage
		if (wrapper == null) { // no null entries
			return false;
		}
		if (isDuplicateEntry(wrapper)) { // no self references
			return false;
		}
		if (wrapper.isKind(Kind.METADATA) && this.isKind(Kind.METADATA)) { // allow metadata mergers
			return true;
		}
		if (wrapper.value == null && wrapper.isKind(Kind.VALUE)) { // no null values
			return false;
		}
		if (wrapper.isType(Type.UNKNOWN)) { // no empty objects
			return false;
		}
		return true; // must be good if we made it through the gauntlet
	}
	public boolean isDuplicateEntry(JsonWrapper wrapper) {
		// TODO asdf this does not check higher level parents and deeper children but it could if we need it.
		boolean duplicate = wrapper == this || childrenList.contains(wrapper) && childrenMap.values().contains(wrapper);
		if ( duplicate ) {
			throw new UnsupportedOperationException("May not add parent to itself nor a child more than once.");
		}
		
		return duplicate;
	}

	/**
	 * Identifies whether or not the {@link JsonWrapper}'s content is a hash or array.
	 *
	 * @return boolean is this a JSON object
	 */
	public boolean isObject() { // TODO asdf this is confusing because of Java Object vs JS Object is a Java Map
		if (type == Type.UNKNOWN) { // TODO asdf if type is unknown or map might be good 
			return isKind(Kind.OBJECT) && childrenList.isEmpty() && ! childrenMap.isEmpty();
		}
		return isType(Type.MAP);
	}
	public boolean isList() {
		if (type == Type.UNKNOWN) { // TODO asdf if type is unknown or list might be good 
			return isKind(Kind.OBJECT) && ! childrenList.isEmpty() && childrenMap.isEmpty();
		}
		return isType(Type.LIST);
	}
	public boolean isValue() {
		return isKind(Kind.VALUE) && value != null;
	}
	public boolean isMetadata() {
		return isKind(Kind.METADATA);
	}
	public boolean hasMetadata() {
		return metadata!=null && (metadata.isObject() || metadata.isList());
	}

	/**
	 * Stream of wrapped object or list.
	 *
	 * @return Stream of wrapped object or list.
	 */
	public Stream<JsonWrapper> stream() {
		Stream<JsonWrapper> stream = Stream.of(this);
		if (isValue()) {
			LinkedList<JsonWrapper> valueList = new LinkedList<JsonWrapper>();
			valueList.add(this);
			stream = valueList.stream();
		} else if (isList()) {
			stream = childrenList.stream();
		} else {
			stream = childrenMap.entrySet()
					.stream()
					.map(entry -> {
						return entry.getValue();
					});
		}
		return stream;
	}

	/**
	 * String representation of the {@link JsonWrapper}.
	 *
	 * @return
	 */
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

	public Object toObject() { // TODO asdf refactor name
		if (isValue()) {
			return value;
		} else if (isList()) {
			return childrenList;
		}
		return childrenMap;
	}

	/**
	 * Convenience method to get the JsonWrapper's content as an input stream.
	 *
	 * @return input stream containing serialized json
	 */
	public Source toSource() {
		byte[] qppBytes = toString().getBytes(StandardCharsets.UTF_8);
		return new InputStreamSupplierSource("QPP", new ByteArrayInputStream(qppBytes));
	}

	void attachMetadata(Node node) { // TODO asdf refactor name
		attachMetadata(node, "");
	}

	void attachMetadata(Node node, String encodeLabel) { // TODO asdf refactor name
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
		addMetaMap(metadata);
	}

	public JsonWrapper getMetadata() {
		return metadata; // TODO asdf if this is metdata return this otherwise return metadata?
	}

	void mergeMetadata(JsonWrapper otherWrapper, String encodeLabel) {
		JsonWrapper metadata = otherWrapper.isMetadata() ?otherWrapper : otherWrapper.getMetadata();
		metadata.stream().forEach(other -> {
			other.put(ENCODING_KEY, encodeLabel);
			
			if (isMetadata()) {
				put(other);
			} else {
				addMetaMap(other);
			}
		});
	}

	void mergeMetadata(Map<String, String> otherMeta) { // TODO asdf refactor remove?
		JsonWrapper metadata = new JsonWrapper();
		otherMeta.entrySet().stream().forEach(entry ->{
			String key = entry.getKey();
			String value = entry.getValue();
			metadata.put(key, value);
		});
		addMetaMap(metadata);
	}
	
	public void addMetaMap(JsonWrapper metadata) { // TODO asdf refator name
		this.metadata.put(metadata.isKind(Kind.METADATA) ?metadata :metadata.metadata);
	}

	public void putMetadata(String name, String value) { // TODO asdf check consistent with other metadata actions
		metadata.put(name, value);
	}
	
	/**
	 * @return the count of entries in the wrapper
	 */
	public int size() {
		if (isType(Type.UNKNOWN)) {
			return 0;
		}
		if (isValue()) {
			return 1; // TODO asdf is this the desired size for a value instance?
		} 
		if (isList()) {
			return childrenList.size();
		}
		return childrenMap.size();
	}
}
