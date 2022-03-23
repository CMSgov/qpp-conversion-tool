package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.truth.Truth;
import com.jayway.jsonpath.JsonPath;

import gov.cms.qpp.conversion.encode.JsonWrapper.Kind;
import gov.cms.qpp.conversion.encode.JsonWrapper.Type;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.util.JsonHelper;

class JsonWrapperTest {

	private ObjectWriter ow = JsonWrapper.standardWriter();
	private JsonWrapper objectObjWrapper;
	private JsonWrapper objectStrWrapper;
	private JsonWrapper listObjWrapper;
	private JsonWrapper listStrWrapper;
	private JsonWrapper unfilteredMetaWrapper;

	@BeforeEach
	void before() {
		objectObjWrapper = new JsonWrapper();
		objectStrWrapper = new JsonWrapper();
		listObjWrapper   = new JsonWrapper();
		listStrWrapper   = new JsonWrapper();
		unfilteredMetaWrapper = new JsonWrapper();
	}

	@Test
	void testCopyConstructor() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put("you're an array");
		JsonWrapper copyWrapper = new JsonWrapper(wrapper);

		assertThat(copyWrapper.toString()).isEqualTo(wrapper.toString());
	}

	@Test
	void testInitAsList() {
		assertWithMessage("Object should be empty until the first put").
				that(listStrWrapper.isType(Type.LIST)).isFalse();
		listStrWrapper.put("name");
		Object list1 = listStrWrapper.toObject();
		assertThat(list1).isInstanceOf(List.class);
		listStrWrapper.put("value");
		Object list2 = listStrWrapper.toObject();
		assertWithMessage("The internal instance should not change upon addition put")
				.that(list1)
				.isEqualTo(list2);
	}

	@Test
	void testInitAsObject() {
		assertWithMessage("Object should be null until the first put")
				.that(objectStrWrapper.isMap()).isFalse();
		objectStrWrapper.put("name", "value");
		Object obj1 = objectStrWrapper.toObject();
		assertThat(obj1).isInstanceOf(Map.class);
		objectStrWrapper.put("name", "value");
		Object obj2 = objectStrWrapper.toObject();
		assertWithMessage("The internal instance should not change upon addition put")
				.that(obj1)
				.isEqualTo(obj2);
	}

	@Test
	void testtoObject_map() {
		objectStrWrapper.put("name1", "value");
		assertThat(objectStrWrapper.toObject())
				.isInstanceOf(Map.class);
		assertThat(objectStrWrapper.getString("name1"))
				.isEqualTo("value");

//		Object obj = new Object(); TODO asdf
//		objectObjWrapper.put("name2", obj);
//		assertThat(obj)
//				.isEqualTo(((Map<?,?>)objectObjWrapper.toObject()).get("name2"));
//		assertThat(objectObjWrapper.toObject())
//				.isInstanceOf(Map.class);
	}	

	@Test
	void testtoObject_list() {
		listStrWrapper.put("name");
		Object obj = listStrWrapper.toObject();
		assertThat(obj).isInstanceOf(List.class);
		assertThat(obj.toString()).contains("name");
//		Object obj = new Object(); TODO asdf
//		listObjWrapper.put(obj);
//		assertThat(((List<?>)listObjWrapper.toObject()))
//				.contains(obj);
	}	

	@Test
	void testIsObject_true() {
		assertWithMessage("should not be an object container until first put")
				.that(objectStrWrapper.isType(Type.MAP)).isFalse();
		objectStrWrapper.put("name", "value");
		assertWithMessage("should be an object container after first put")
				.that(objectStrWrapper.isMap()).isTrue();
//		objectObjWrapper.put("name", new Object()); TODO asdf
//		assertWithMessage("should be an object container after first map put")
//				.that(objectObjWrapper.isObject()).isTrue();
	}

	@Test
	void testIsObject_false() {
		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isMap())
				.isFalse();
		listStrWrapper.put("name");
		assertWithMessage("should not be an object container after first list put")
				.that(listStrWrapper.isMap())
				.isFalse();

		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isMap())
				.isFalse();
//		listObjWrapper.put(new Object()); TODO asdf
//		assertWithMessage("should not be an object container after first list put")
//				.that(listStrWrapper.isObject())
//				.isFalse();
	}

	@Test
	@DisplayName("should validate and fail null passed as integer")
	void testValidInterIsNull() {
		Throwable exception = assertThrows(EncodeException.class, () -> {
			objectObjWrapper.validInteger(null);
		});
		assertThat(exception).hasCauseThat().isInstanceOf(NumberFormatException.class);
	}

	@Test
	@DisplayName("should validate and fail non-numeric string passed as integer")
	void testValidInterIsNotNumber() {
		Throwable exception = assertThrows(EncodeException.class, () -> {
			objectObjWrapper.validInteger("meep");
		});
		assertThat(exception).hasCauseThat().isInstanceOf(NumberFormatException.class);
	}

	@Test
	void testValidDateYyyyMmDd() throws Exception {
		ensureDateIsValid("19690720");
	}

	@Test
	void testValidDateYyyySlashMmSlashDd() throws Exception {
		ensureDateIsValid("1969/07/20");
	}

	@Test
	void testValidDateYyyyDashMmDashDd() throws Exception {
		ensureDateIsValid("1969-07-20");
	}

	@Test
	void testValidDateYyyyDashMmDashDdThhColonMmColonSsZ() {
		ensureDateIsValid("2018-01-26T15:35:30.685Z");
	}

	@Test
	void testValidDateFromInstant() throws Exception {
		ensureDateIsValid(Instant.now().toString());
	}

	@Test
	void testValidDateFromLocalDate() throws Exception {
		ensureDateIsValid(LocalDate.now().toString());
	}

	@Test
	void testValidDateFromLocalDateTime() throws Exception {
		ensureDateIsValid(LocalDateTime.now().toString());
	}

	@Test
	void testValidDateWithSeconds1() throws Exception {
		ensureDateIsValid("20170101000000");
	}

	@Test
	void testValidDateWithSeconds2() throws Exception {
		ensureDateIsValid("20171231235959");
	}

	@Test
	void testValidDateFullyQualified() {
		ensureDateIsValid("2018-01-22T20:09:39.949Z");
	}

	private void ensureDateIsValid(String date) {
		objectObjWrapper.putDate(date);
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.toObject()))
				.isNotEmpty();
	}

	@Test
	@DisplayName("should invalidate null value for date")
	void testValidDateNullInvalid() throws Exception {
		Assertions.assertThrows(EncodeException.class, () -> objectObjWrapper.putDate(null));
	}

	@Test
	void testValidDateYyMmDdIsInvalid() throws Exception {
		Assertions.assertThrows(EncodeException.class, () -> objectObjWrapper.putDate("690720"));
	}

	@Test
	void testValidDateRandomStringIsInvalid() throws Exception {
		Assertions.assertThrows(EncodeException.class, () -> objectObjWrapper.putDate(UUID.randomUUID().toString()));
	}

	@Test
	void testNullObjectPut() {
		listStrWrapper.put( (JsonWrapper) null );
		assertWithMessage("should not be an object list container yet")
			.that(listStrWrapper.isType(Type.LIST)).isFalse();
		assertWithMessage("should not be an object map container yet")
			.that(listStrWrapper.isType(Type.MAP)).isFalse();
	}

	@Test
	void testJackson_simpleObject() throws Exception {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("name1", "value1");
		map.put("name2", "value2");

		String json = ow.writeValueAsString(map);

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : \"value2\"\n}";
		assertWithMessage("expect a simple object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testJackson_objectWithArray() throws Exception {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name1", "value1");
		map.put("name2", new String[] {"A","B","C"});
		map.put("name3", "value3");

		String json = ow.writeValueAsString(map);

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : [ \"A\", \"B\", \"C\" ],\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect array to use [] rather than {} block")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testJackson_objectWithList() throws Exception {
		List<String> list = new LinkedList<>();
		list.add("A");
		list.add("B");
		list.add("C");

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name1", "value1");
		map.put("name2", list);
		map.put("name3", "value3");

		String json = ow.writeValueAsString(map);

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : [ \"A\", \"B\", \"C\" ],\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect list to look like array")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testJackson_objectWithChild() throws Exception {
		Map<String, Object> obj = new LinkedHashMap<>();
		obj.put("obj1", "A");
		obj.put("obj2", "B");
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name1", "value1");
		map.put("name2", obj);
		map.put("name3", "value3");

		String json = ow.writeValueAsString(map);
		
		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : {\n    \"obj1\" : \"A\",\n    \"obj2\" : \"B\"\n  },\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect comma after child and no comma after last value pair")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testJackson_objectWithChild_commaAndOrder() throws Exception {
		Map<String, Object> obj = new LinkedHashMap<>();
		obj.put("obj1", "A");
		obj.put("obj2", "B");
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("name1", "value1");
		map.put("name3", "value3");
		map.put("name2", obj);

		String json = ow.writeValueAsString(map);

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name3\" : \"value3\",\n" +
				"  \"name2\" : {\n    \"obj1\" : \"A\",\n    \"obj2\" : \"B\"\n  }\n}";
		assertWithMessage("expect no comma expected after the child and order as inserted")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_simpleObject() throws Exception {
		objectStrWrapper.put("name1", "value1");
		objectStrWrapper.put("name2", "value2");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : \"value2\"\n}";
		assertWithMessage("expect a simple object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_integerArray() throws Exception {
		objectStrWrapper.put("value1");
		objectStrWrapper.putInteger("100");

		String json = objectStrWrapper.toString();

		String expect = "[ \"value1\", 100 ]";
		assertWithMessage("expect a integer array of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_floatArray() throws Exception {
		objectStrWrapper.put("value1");
		objectStrWrapper.putFloat("1.01");

		String json = objectStrWrapper.toString();

		String expect = "[ \"value1\", 1.01 ]";
		assertWithMessage("expect a float value array of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testBadKeyedPutDate_exception() {
		assertThrows(EncodeException.class,
				() -> objectObjWrapper.putDate("A date which will live in infamy", "December 7, 1941"));
	}

	@Test
	void testBadPutDate_exception() {
		assertThrows(EncodeException.class, () -> objectObjWrapper.putDate("December 7, 1941"));
	}

	@Test
	void testCheckState_objectThenList() {
		objectStrWrapper.put("name", "value");
		assertThrows(IllegalStateException.class, () -> objectStrWrapper.put("value"));
	}

	@Test
	void testCheckState_listThenLObject() {
		listStrWrapper.put("value");
		assertThrows(IllegalStateException.class,
				() -> listStrWrapper.put("name", "value"));
	}

	@Test
	void testToString_integerParseExcpetion() {
		assertThrows(EncodeException.class,
				() -> objectStrWrapper.putInteger("name2", "nope"));
	}

	@Test
	void testToString_integerParseExcpetion2() {
		assertThrows(EncodeException.class, () -> objectStrWrapper.putInteger("nope"));
	}

	@Test
	void testToString_floatParseExcpetion() {
		assertThrows(EncodeException.class, () -> objectStrWrapper.putFloat("nope"));
	}

	@Test
	void testToString_floatParseExcpetion2() {
		assertThrows(EncodeException.class, () -> objectStrWrapper.putFloat("name","nope"));
	}

	@Test
	void testToString_booleanParseExcpetion() {
		assertThrows(EncodeException.class, () -> objectStrWrapper.putBoolean("nope"));
	}

	@Test
	void testToString_booleanParseExcpetion2() {
		assertThrows(EncodeException.class, () -> objectStrWrapper.putBoolean("name","nope"));
	}

	@Test
	void testValidBoolean() throws Exception {
		assertThat(objectStrWrapper.validBoolean("True")).isTrue();
		assertThat(objectStrWrapper.validBoolean("Yes")).isTrue();
		assertThat(objectStrWrapper.validBoolean("Y")).isTrue();
		assertThat(objectStrWrapper.validBoolean("false")).isFalse();
		assertThat(objectStrWrapper.validBoolean("no")).isFalse();
		assertThat(objectStrWrapper.validBoolean("N")).isFalse();
	}

	@Test
	void testToString_booleanArray() throws Exception {
		objectStrWrapper.put("True"); // as string where case is preserved
		objectStrWrapper.putBoolean("True");
		objectStrWrapper.putBoolean("TRUE");
		objectStrWrapper.putBoolean("true");
		objectStrWrapper.putBoolean("trUe");

		String json = objectStrWrapper.toString();

		String expect = "[ \"True\", true, true, true, true ]";
		assertWithMessage("expect a boolean value object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_objectWithArray() throws Exception {
		objectObjWrapper.put("name1", "value1");
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put("A").put("B").put("C");
		objectObjWrapper.put("name2", wrapper);
		objectObjWrapper.put("name3", "value3");

		String json = objectObjWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : [ \"A\", \"B\", \"C\" ],\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect array to use [] rather than {} block")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_integerObject() throws Exception {
		objectStrWrapper.put("name1", "value1");
		objectStrWrapper.putInteger("name2", "100");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 100\n}";
		assertWithMessage("expect a integer object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_floatObject() throws Exception {
		objectStrWrapper.put("name1", "value1");
		objectStrWrapper.putFloat("name2", "1.01");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 1.01\n}";
		assertWithMessage("expect a float value object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_booleanObject() throws Exception {
		objectStrWrapper.put("name", "True"); // as string where case is preserved
		objectStrWrapper.putBoolean("name1", "True");
		objectStrWrapper.putBoolean("name2", "TRUE");
		objectStrWrapper.putBoolean("name3", "true");
		objectStrWrapper.putBoolean("name4", "trUe");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name\" : \"True\",\n  \"name1\" : true,\n  \"name2\" : true,\n  \"name3\" : true,\n  \"name4\" : true\n}";
		assertThat(json).isEqualTo(expect);
	}

	@Test
	void testStripWrapper_list() {
		listStrWrapper.put("A");
		listStrWrapper.put("B");
		listStrWrapper.put("C");

		Object result = objectObjWrapper.stripWrapper(listStrWrapper);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(List.class);
	}

	@Test
	void testStripWrapper_object() {
		objectStrWrapper.put("obj1", "A");
		objectStrWrapper.put("obj2", "B");

		Object result = objectObjWrapper.stripWrapper(objectStrWrapper);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Map.class);
	}

	@Test
	void testToString_objectWithList() throws Exception {
		listStrWrapper.put("A");
		listStrWrapper.put("B");
		listStrWrapper.put("C");

		objectObjWrapper.put("name1", "value1");
		objectObjWrapper.put("name2", listStrWrapper);
		objectObjWrapper.put("name3", "value3");

		String actual = objectObjWrapper.toString();
		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : [ \"A\", \"B\", \"C\" ],\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect list to look like array")
				.that(actual)
				.isEqualTo(expect);
	}

	@Test
	void testToString_objectWithChild() throws Exception {
		objectStrWrapper.put("obj1", "A");
		objectStrWrapper.put("obj2", "B");

		objectObjWrapper.put("name1", "value1");
		objectObjWrapper.put("name2", objectStrWrapper);
		objectObjWrapper.put("name3", "value3");

		String json = objectObjWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : {\n    \"obj1\" : \"A\",\n    \"obj2\" : \"B\"\n  },\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect comma after child and no comma after last value pair")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_objectWithChild_commaAndOrder() throws Exception {
		objectStrWrapper.put("obj1", "A");
		objectStrWrapper.put("obj2", "B");

		objectObjWrapper.put("name1", "value1");
		objectObjWrapper.put("name3", "value3");
		objectObjWrapper.put("name2", objectStrWrapper);

		String json = objectObjWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name3\" : \"value3\",\n" +
				"  \"name2\" : {\n    \"obj1\" : \"A\",\n    \"obj2\" : \"B\"\n  }\n}";
		assertWithMessage("expect no comma expected after the child and order as inserted")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testValueRetrieval() throws Exception {
		objectStrWrapper.put("obj1", "A");
		objectStrWrapper.putInteger("obj2", "1");
		objectStrWrapper.putFloat("obj3", "1.1");
		objectStrWrapper.putBoolean("obj4", "false");

		assertThat(objectStrWrapper.getString("obj1"))
				.isEqualTo("A");
		assertThat(objectStrWrapper.getInteger("obj2"))
				.isEqualTo(1);
		assertThat(objectStrWrapper.getFloat("obj3"))
				.isEqualTo(1.1F);
		assertThat(objectStrWrapper.getBoolean("obj4"))
				.isFalse();
	}

	@Test
	void metadataFiltered() throws IOException {
		//setup
		String shouldSerialize = "mawp";
		String shouldNotSerialize = JsonWrapper.METADATA_HOLDER;
		String shouldAlsoNotSerialize = "any metadata";
		objectObjWrapper.put(shouldSerialize, shouldSerialize);
		objectObjWrapper.putMetadata(shouldNotSerialize, shouldNotSerialize);
		objectObjWrapper.putMetadata(shouldAlsoNotSerialize, shouldAlsoNotSerialize);

		//when
		String json = objectObjWrapper.copyWithoutMetadata().toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode obj = mapper.readTree(json);

		//then
		assertThat(shouldSerialize)
				.isEqualTo(obj.findValue(shouldSerialize).asText());
		assertThat(obj.findValue(shouldNotSerialize))
				.isNull();
		assertThat(obj.findValue(shouldAlsoNotSerialize))
				.isNull();
	}

	@Test
	void metadataUnfiltered() throws IOException {
		//setup
		String shouldSerialize = "mawp";
		String shouldAlsoSerialize = "metadata_meep";
		unfilteredMetaWrapper.put(shouldSerialize, shouldSerialize);
		unfilteredMetaWrapper.put(shouldAlsoSerialize, shouldAlsoSerialize);

		//when
		String json = unfilteredMetaWrapper.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode obj = mapper.readTree(json);

		//then
		assertThat(shouldSerialize)
				.isEqualTo(obj.findValue(shouldSerialize).asText());
		assertThat(shouldAlsoSerialize)
				.isEqualTo(obj.findValue(shouldAlsoSerialize).asText());
	}
	
	@Test
	void metadata_toString() {
		// setup
		String data = "mawp";
		String meta1 = "metadata";
		String meta2 = "any-metadata";
		objectObjWrapper.put(data, data);
		objectObjWrapper.putMetadata(meta1, meta1);
		objectObjWrapper.putMetadata(meta2, meta2);
		
		// test
		String json = objectObjWrapper.getMetadata().toString().replaceAll("\\s", "");
		String expected = "{\"metadata\":\"metadata\",\"any-metadata\":\"any-metadata\"}";
		
		// assert
		assertThat(json).isEqualTo(expected);
	}
	
	@Test
	void metadata_toString_list() {
		// setup
		String meta1 = "metadata";
		String meta2 = "any-metadata";
		objectObjWrapper.putMetadata(meta1, meta1);
		objectStrWrapper.putMetadata(meta2, meta2);
		
		listObjWrapper.addMetadata(objectObjWrapper);
		listObjWrapper.addMetadata(objectStrWrapper);
		
		// test
		String json = listObjWrapper.getMetadata().toString().replaceAll("\\s", "");
		String expected = "[{\"metadata\":\"metadata\"},{\"any-metadata\":\"any-metadata\"}]";
		
		// assert
		assertThat(json).isEqualTo(expected);
	}

	@Test
	@SuppressWarnings("unchecked")
	void testContentStream() {
		objectObjWrapper.put("meep", "mawp");
		InputStream content = objectObjWrapper.toSource().toInputStream();
		Map<String, String> contentMap = JsonHelper.readJson(content, Map.class);
		assertThat(contentMap.get("meep")).isEqualTo("mawp");
	}

	@Test
	void testObjectStream_count() {
		objectObjWrapper.put("name","value");
		assertThat(objectObjWrapper.stream().count()).isEqualTo(1);
	}
	@Test
	void testListStream_count() {
		listStrWrapper.put("value");
		assertThat(listStrWrapper.stream().count()).isEqualTo(1);
	}
	@Test
	void testValueStream_count() {
		listStrWrapper = new JsonWrapper("value");
		assertThat(listStrWrapper.stream().count()).isEqualTo(1);
	}

	@Test
	void testListStreamNonMap() { 
		JsonWrapper listWrapper = new JsonWrapper();
		listWrapper.put(new JsonWrapper());
		assertThat(listWrapper.size())
				.isEqualTo(0);
		listWrapper.put(new JsonWrapper("value"));
		assertThat(listWrapper.size())
				.isEqualTo(1);
	}

	@Test
	void testListStreamMap() {
		JsonWrapper listWrapper = new JsonWrapper();
		listWrapper.put("a");
		listWrapper.put("b");
		assertThat(listWrapper.stream().count())
				.isEqualTo(2);
	}

	@Test
	void encodeDefaultNode() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put(TemplateId.PLACEHOLDER.toString(), new JsonWrapper());
		assertThat(wrapper.toString()).hasLength(3);
	}
	
	@Test
	void checkState_value() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper child = new JsonWrapper("value");
		
		boolean actual = wrapper.checkState(child);
		
		assertWithMessage("checkState should accept the child that has value")
			.that(actual).isTrue();
	}
	@Test
	void checkState_nullValue() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper child = new JsonWrapper();
		
		boolean actual = wrapper.checkState(child);
		
		assertWithMessage("checkState should not accept the child that has no value")
			.that(actual).isFalse();
	}
	@Test
	void checkState_nullInstance() {
		JsonWrapper wrapper = new JsonWrapper();
		
		boolean actual = wrapper.checkState(null);
		
		assertWithMessage("checkState should not accept the child that has null wrapper")
			.that(actual).isFalse();
	}
	@Test
	void checkState_addSelf() {
		JsonWrapper wrapper = new JsonWrapper();

		boolean actual = wrapper.checkState(wrapper);

		assertWithMessage("Expected warning when checking state of adding self instance to a wrapper.")
			.that(actual).isFalse();
	}
	@Test
	void checkState_addChildTwice_toMap() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper child = new JsonWrapper("child");
		wrapper.put("name", child);

		boolean actual = wrapper.checkState(wrapper);

		assertWithMessage("Expected warning when checking state adding child instance more than once.")
			.that(actual).isFalse();
	}
	@Test
	void checkState_addChildTwice_toList() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper child = new JsonWrapper("child");
		wrapper.put(child);
		
		boolean actual = wrapper.checkState(wrapper);
		
		assertWithMessage("Expected warning when checking state adding child instance more than once.")
			.that(actual).isFalse();
	}
	@Test
	void checkState_addMetadata_empty() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper metadata = new JsonWrapper(Kind.METADATA);
		
		boolean actual = wrapper.checkState(metadata);
		
		assertWithMessage("checkState should not accept empty metadata")
			.that(actual).isFalse();
	}
	@Test
	void checkState_addMetadata() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper metadata = new JsonWrapper(Kind.METADATA);
		metadata.put("name","value");
		
		boolean actual = wrapper.checkState(metadata);
		
		assertWithMessage("checkState should accept empty metadata")
			.that(actual).isTrue();
	}
	@Test
	void checkState_addUnknown() {
		JsonWrapper wrapper = new JsonWrapper();
		JsonWrapper unknown = new JsonWrapper(Kind.CONTAINER);
		
		boolean actual = wrapper.checkState(unknown);
		
		assertWithMessage("checkState should not accept unknown wrappers")
			.that(actual).isFalse();
	}

//	when(gen.writeBoolean(any(Boolean.class)))
	
	@Test
	void hasValue_true() throws Exception {
		JsonWrapper value = new JsonWrapper(Boolean.TRUE);
		boolean actual = Type.BOOLEAN.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
		
		value = new JsonWrapper("01/01/2020");
		actual = Type.DATE.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
		
		value = new JsonWrapper(1);
		actual = Type.INTEGER.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
		
		value = new JsonWrapper(1.1f);
		actual = Type.FLOAT.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
				
		value = new JsonWrapper("value");
		actual = Type.STRING.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
		
		value = new JsonWrapper().put("name", "value");
		actual = Type.MAP.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
				
		value = new JsonWrapper().put("value");
		actual = Type.LIST.hasValue(value);
		assertWithMessage("Instance should have value")
			.that(actual).isTrue();
	}
	
	@Test
	void hasValue_false() throws Exception {
		JsonWrapper value = new JsonWrapper((String)null);
		boolean actual = Type.STRING.hasValue(value);
		assertWithMessage("Instance should NOT have value because of null")
			.that(actual).isFalse();
		
		value = new JsonWrapper();
		actual = Type.MAP.hasValue(value);
		assertWithMessage("Instance should NOT have value because of UNKNOWN")
			.that(actual).isFalse();
	}		
	
	@Test
	void enumTypeBoolean() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper(Boolean.TRUE);
		
		Type.BOOLEAN.json(value, gen);
		
		verify(gen, times(1)).writeBoolean(any(Boolean.class));
	}
	@Test
	void enumTypeBoolean_empty() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper((String)null);
		
		Type.BOOLEAN.json(value, gen);
		verify(gen, times(0)).writeBoolean(any(Boolean.class));
		
		Type.BOOLEAN.json(null, gen);
		verify(gen, times(0)).writeBoolean(any(Boolean.class));
	}
	@Test
	void enumTypeDate() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper("01/01/2020");
		
		Type.DATE.json(value, gen);
		
		verify(gen, times(1)).writeString(any(String.class));
	}
	@Test
	void enumTypeDate_empty() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper((String)null);
		
		Type.DATE.json(value, gen);
		verify(gen, times(0)).writeBoolean(any(Boolean.class));
		
		Type.DATE.json(null, gen);
		verify(gen, times(0)).writeBoolean(any(Boolean.class));
	}
	@Test
	void enumTypeInteger() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper(1);
		
		Type.INTEGER.json(value, gen);
		
		verify(gen, times(1)).writeNumber(any(Integer.class));
	}
	@Test
	void enumTypeInteger_empty() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper((String)null);
		
		Type.INTEGER.json(value, gen);
		verify(gen, times(0)).writeNumber(any(Integer.class));

		Type.INTEGER.json(null, gen);
		verify(gen, times(0)).writeNumber(any(Integer.class));
	}
	@Test
	void enumTypeFloat() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper(1.1f);
		
		Type.FLOAT.json(value, gen);
		
		verify(gen, times(1)).writeNumber(any(Float.class));
	}
	@Test
	void enumTypeFloat_empty() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper((String)null);
		
		Type.FLOAT.json(value, gen);
		verify(gen, times(0)).writeNumber(any(Float.class));

		Type.FLOAT.json(null, gen);
		verify(gen, times(0)).writeNumber(any(Float.class));
	}
	@Test
	void enumTypeMap() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper().put("name","value");
		
		Type.MAP.json(value, gen);
		
		verify(gen, times(1)).writeObject(any(Map.class));
	}
	@Test
	void enumTypeMap_empty() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper((String)null);
		
		Type.MAP.json(value, gen);
		verify(gen, times(0)).writeObject(any(Map.class));

		Type.MAP.json(null, gen);
		verify(gen, times(0)).writeObject(any(Map.class));
		
		value = new JsonWrapper();
		Type.MAP.json(null, gen);
		verify(gen, times(0)).writeObject(any(Map.class));
	}
	@Test
	void enumTypeList() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper().put("value");
		
		Type.LIST.json(value, gen);
		
		verify(gen, times(1)).writeObject(any(List.class));
	}
	@Test
	void enumTypeList_empty() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper((String)null);
		
		Type.LIST.json(value, gen);
		verify(gen, times(0)).writeObject(any(List.class));

		Type.LIST.json(null, gen);
		verify(gen, times(0)).writeObject(any(List.class));
		
		value = new JsonWrapper();
		Type.LIST.json(null, gen);
		verify(gen, times(0)).writeObject(any(List.class));
	}
	
	@Test
	void enumTypeUnknow() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper value = new JsonWrapper("mock");
		// this is odd coverage because only UNKNOWN calls the empty noop impls
		Type.UNKNOWN.json(value, gen);
		Type.UNKNOWN.metadata(value, gen);
	}
	
	@Test
	void metadataMap_noMetadata() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		InOrder order = Mockito.inOrder(gen);

		// wrapper uses a LinkedHashMap to preserve order
		JsonWrapper value = new JsonWrapper().put("name","value").put("other","data");
		
		Type.MAP.metadata(value, gen);

		// cool, I tested taking these our of order - works like a charm
		order.verify(gen, times(1)).writeStartObject();
		order.verify(gen, times(2)).writeObjectField(any(), any());
		order.verify(gen, times(1)).writeEndObject();
	}
	@Test
	void metadataMap_withMetadata() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		InOrder order = Mockito.inOrder(gen);
		
		JsonWrapper value = new JsonWrapper().put("name","value").putMetadata("meta", "data");;
		
		Type.MAP.metadata(value, gen);

		// cool, I tested taking these our of order - works like a charm
		order.verify(gen, times(1)).writeStartObject();
		order.verify(gen, times(1)).writeObjectField(JsonWrapper.METADATA_HOLDER, value.getMetadata());
		// mock instance does not call inner actions - how could it if it is only a MOCK!

		order.verify(gen, times(1)).writeObjectField(any(),any());
		order.verify(gen, times(1)).writeEndObject();
	}
	@Test
	void metadataMap_throwsIOE() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		Mockito.doThrow(IOException.class).when(gen).writeObjectField(any(), any());

		// wrapper uses a LinkedHashMap to preserve order
		JsonWrapper value = new JsonWrapper().put("name","value");
		
		assertThrows(RuntimeException.class, () -> {Type.MAP.metadata(value, gen);});
	}

	@Test
	void metadataList_noMetadata() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		InOrder order = Mockito.inOrder(gen);

		// wrapper uses a LinkedList to preserve order
		JsonWrapper value = new JsonWrapper().put("value").put("data");
		JsonWrapper item1 = value.get(0);
		JsonWrapper item2 = value.get(1);
		
		Type.LIST.metadata(value, gen);

		// cool, I tested taking these our of order - works like a charm
		order.verify(gen, times(1)).writeStartArray();
		order.verify(gen, times(1)).writeObject(item1);
		order.verify(gen, times(1)).writeObject(item2);
		order.verify(gen, times(1)).writeEndArray();
	}
	@Test
	void metadataList_withMetadata() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		InOrder order = Mockito.inOrder(gen);
		
		JsonWrapper value = new JsonWrapper().put("value").putMetadata("meta", "data");;
		
		JsonWrapper item1 = value.get(0);
		
		Type.LIST.metadata(value, gen);

		// cool, I tested taking these our of order - works like a charm
		order.verify(gen, times(1)).writeStartArray();
		order.verify(gen, times(1)).writeObject(value.getMetadata());
		// mock instance does not call inner actions - how could it if it is only a MOCK!
//		order.verify(gen, times(1)).writeStartObject();
//		order.verify(gen, times(1)).writeObjectField("meta","data");
//		order.verify(gen, times(1)).writeEndObject();
		order.verify(gen, times(1)).writeObject(item1);
		order.verify(gen, times(1)).writeEndArray();
	}
	@Test
	void metadataList_throwsIOE() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);

		// wrapper uses a LinkedList to preserve order
		JsonWrapper value = new JsonWrapper().put("value");
		JsonWrapper item1 = value.get(0);
		
		Mockito.doThrow(IOException.class).when(gen).writeObject(item1);
		
		assertThrows(RuntimeException.class, () -> {Type.LIST.metadata(value, gen);});
	}

	@Test
	void test_JsonWrapperMetadataSerilizer_jsonContainer_noMetadata() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper.Type map = mock(JsonWrapper.Type.class);
		JsonWrapper value = new JsonWrapper().put("value").setType(map);
		
		JsonWrapper.JsonWrapperMetadataSerilizer metaSer = new JsonWrapper.JsonWrapperMetadataSerilizer();
		metaSer.jsonContainer(value, gen, null);
		
		// should call the super class handling if metadata is absent from the wrapper
		verify(map, times(0)).metadata(value, gen);
		verify(map, times(1)).json(value, gen);
	}
	@Test
	void test_JsonWrapperMetadataSerilizer_jsonContainer_withMetadata() throws Exception {
		JsonGenerator gen = mock(JsonGenerator.class);
		JsonWrapper.Type map = mock(JsonWrapper.Type.class);
		JsonWrapper value = new JsonWrapper().put("value").putMetadata("meta", "data");
		value.setType(map);
		
		JsonWrapper.JsonWrapperMetadataSerilizer metaSer = new JsonWrapper.JsonWrapperMetadataSerilizer();
		metaSer.jsonContainer(value, gen, null);

		// should call the metadata method on the type instance if metadata is added to the wrapper
		verify(map, times(1)).metadata(value, gen);
		verify(map, times(0)).json(value, gen);
	}
	
	@Test
	void Constructor_throwsUnsupported() throws Exception {
		assertThrows(UnsupportedOperationException.class, () -> {new JsonWrapper(Kind.VALUE);});
	}
	
	@Test
	void getKind() throws Exception {
		assertWithMessage("getKind should return the wrapper Kind")
			.that(new JsonWrapper().getKind())
			.isEqualTo(Kind.CONTAINER);
	}
	
	@Test
	void clear_list() {
		JsonWrapper list = new JsonWrapper().put("value").putMetadata("meta", "data");
		assertWithMessage("Should have size 1 with a value added")
			.that(list.size())
			.isEqualTo(1);
		assertWithMessage("Should have metadata")
			.that(list.hasMetadata())
			.isTrue();
		list.clear();
		assertWithMessage("Should have zero size after clear")
			.that(list.size())
			.isEqualTo(0);
		assertWithMessage("Metadata should be cleared")
			.that(list.hasMetadata())
			.isFalse();
	}
	@Test
	void clear_map() {
		JsonWrapper map = new JsonWrapper().put("name","value").putMetadata("meta", "data");
		assertWithMessage("Should have size 1 with a entry added")
			.that(map.size())
			.isEqualTo(1);
		assertWithMessage("Should have metadata")
			.that(map.hasMetadata())
			.isTrue();
		map.clear();
		assertWithMessage("Should have zero size after clear")
			.that(map.size())
			.isEqualTo(0);
		assertWithMessage("Metadata should be cleared")
			.that(map.hasMetadata())
			.isFalse();
	}
	@Test
	void clear_value() {
		JsonWrapper value = new JsonWrapper("value");
		value.clear();
		assertWithMessage("Clear should not clear immmutable values.")
			.that(value.toObject()).isEqualTo("value");
		assertWithMessage("Clear should not clear immmutable values.")
			.that(value.getType()).isEqualTo(Type.STRING);
	}
	
	@Test
	void putDate_valid() {
		String date = "2010/10/20";
		JsonWrapper wrap = new JsonWrapper().putDate("date",date);
		assertWithMessage("Should return entered date")
			.that(wrap.getString("date"))
			.isEqualTo(date.replaceAll("/","-"));
		wrap = new JsonWrapper().putDate(date);
		assertWithMessage("Should return entered date")
			.that(wrap.get(0).toObject())
			.isEqualTo(date.replaceAll("/","-"));
	}
	@Test
	void putDate_invalid() {
		assertThrows(EncodeException.class, () -> {
			new JsonWrapper().putDate("date","not a date");
		});
	}
	
	@Test
	void putInteger() {
		JsonWrapper wrap = new JsonWrapper().put("int",1);
		assertWithMessage("Should return entered integer")
			.that(wrap.getInteger("int"))
			.isEqualTo(1);
		wrap = new JsonWrapper().put(1);
		assertWithMessage("Should return entered integer")
			.that(wrap.get(0).toObject())
			.isEqualTo("1");
	}

	@Test
	void putFloat() {
		JsonWrapper wrap = new JsonWrapper().put("float",1.1f);
		assertWithMessage("Should return entered float")
			.that(wrap.getFloat("float"))
			.isEqualTo(1.1f);
		wrap = new JsonWrapper().put(1.1f);
		assertWithMessage("Should return entered float")
			.that(wrap.get(0).toObject())
			.isEqualTo("1.1");
	}
	
	@Test
	void putBoolean() {
		JsonWrapper wrap = new JsonWrapper().put("bool", true);
		assertWithMessage("Should return entered boolean")
			.that(wrap.getBoolean("bool"))
			.isEqualTo(true);
		wrap = new JsonWrapper().put(true);
		assertWithMessage("Should return entered boolean")
			.that(wrap.get(0).toObject())
			.isEqualTo("true");
	}

	@Test
	void removeValuesFromWrapper() {
		JsonWrapper wrap = new JsonWrapper().put("name", "value");
		wrap.remove("name");
		assertThat(wrap.get("name")).isNull();
	}
	
	@Test
	void getString_valueNotFound() {
		JsonWrapper wrap = new JsonWrapper().put("name", "value");
		assertWithMessage("Should return null for unfound entry rather than NPE")
			.that(wrap.getString("notEntered"))
			.isNull();
	}
	
	@Test
	void getInteger_valueNotFound() {
		JsonWrapper wrap = new JsonWrapper().put("int", 1);
		assertWithMessage("Should return null for unfound entry rather than NPE")
			.that(wrap.getInteger("notEntered"))
			.isNull();
	}
	
	@Test
	void getFloat_valueNotFound() {
		JsonWrapper wrap = new JsonWrapper().put("float", 1.1f);
		assertWithMessage("Should return null for unfound entry rather than NPE")
			.that(wrap.getFloat("notEntered"))
			.isNull();
	}
	
	@Test
	void getBoolean_valueNotFound() {
		JsonWrapper wrap = new JsonWrapper().put("bool", true);
		assertWithMessage("Should return null for unfound entry rather than NPE")
			.that(wrap.getBoolean("notEntered"))
			.isNull();
	}
	
	@Test
	void getIndex_valueNotFound() {
		JsonWrapper wrap = new JsonWrapper().put("value");
		assertWithMessage("Should return null for unfound entry rather than NPE")
			.that(wrap.get(10))
			.isNull();
		assertWithMessage("Should return null for unfound entry rather than NPE")
			.that(wrap.get(-10))
			.isNull();
	}

	@Test
	void isDuplicateEntry_false() {
		String value = "value";
		JsonWrapper wrap = new JsonWrapper().put(value);
		
		boolean actual = wrap.isDuplicateEntry(new JsonWrapper(value));
		assertWithMessage("Expect equal instance of unwrapped to be unique")
			.that(actual).isFalse();
	}

	@Test
	void isDuplicateEntry_self() {
		JsonWrapper wrap = new JsonWrapper().put("value");
		
		assertThrows(UnsupportedOperationException.class,
				()->{wrap.isDuplicateEntry(wrap);},
				"Expect self to be considered duplicate");
	}

	@Test
	void isDuplicateEntry_child() {
		JsonWrapper child = new JsonWrapper().put("value");
		JsonWrapper parentList = new JsonWrapper().put(child);
		
		assertThrows(UnsupportedOperationException.class,
				()->{parentList.isDuplicateEntry(child);},
				"Expect child to be detected duplicate");
		
		JsonWrapper parentMap = new JsonWrapper().put("name",child);
		
		assertThrows(UnsupportedOperationException.class,
				()->{parentMap.isDuplicateEntry(child);},
				"Expect child to be detected duplicate");
	}
	
	@Test
	void toStringWithMetadata() throws Exception {
		JsonWrapper map = new JsonWrapper().put("name", "value");
		map.putMetadata("meta", "data");

		String json = map.toStringWithMetadata();

		String expect = "{\n  \"metadata_holder\" : {\n    \"meta\" : \"data\"\n  },\n  \"name\" : \"value\"\n}";
		assertWithMessage("expect a simple object of JSON")
				.that(json)
				.isEqualTo(expect);
	}
	
	@Test
	void stripWrapper_nonWrapper() {
		String internal = "value";
		Object actual = new JsonWrapper().stripWrapper(internal);
		assertWithMessage("Internal Value of a nonWrapper is self")
			.that(actual)
			.isEqualTo(internal);
	}
	
	@Test
	void getMetadata_notNull() {
		JsonWrapper map = new JsonWrapper().put("name", "value");
		map.putMetadata("meta", "data");
		
		JsonWrapper metadata = map.getMetadata();
		assertWithMessage("must return metadata instance")
			.that(metadata)
			.isInstanceOf(JsonWrapper.class);
	}
	
	
	@Test
	void getMetadata_noMetadataOnMetadata() {
		JsonWrapper map = new JsonWrapper().put("name", "value");
		map.putMetadata("meta", "data");
		
		JsonWrapper metadata = map.getMetadata();
		JsonWrapper metametadata = map.getMetadata().getMetadata();
		assertWithMessage("must return self metadata instance")
			.that(metametadata)
			.isEqualTo(metadata);
	}
	
	@Test
	void addMetadata_notNull() {
		JsonWrapper data = new JsonWrapper().put("name", "value").putMetadata("meta1", "data1");

		assertWithMessage("expect metadata size to be 1")
			.that(data.getMetadata().size()).isEqualTo(1);
		
		data.addMetadata(null);
		
		assertWithMessage("expect metadata size to be 1")
			.that(data.getMetadata().size()).isEqualTo(1);
	}
	
	@Test
	void addMetadata() {
		JsonWrapper data = new JsonWrapper().put("name", "value").putMetadata("meta1", "data1");
		JsonWrapper metadata = new JsonWrapper().putMetadata("meta2", "data2");

		assertWithMessage("expect metadata size to be 1")
			.that(data.getMetadata().size()).isEqualTo(1);
		
		data.addMetadata(metadata);
		
		assertWithMessage("expect metadata size to be 2")
			.that(data.getMetadata().size()).isEqualTo(2);
	}
	
	@Test
	void addMetadata_toMetadata() {
		JsonWrapper data = new JsonWrapper().put("name", "value").putMetadata("meta1", "data1");
		JsonWrapper metadata = new JsonWrapper().putMetadata("meta2", "data2");

		assertWithMessage("expect metadata size to be 1")
			.that(data.getMetadata().size()).isEqualTo(1);
		
		data.getMetadata().addMetadata(metadata);
		
		assertWithMessage("expect metadata size to be 2")
			.that(data.getMetadata().size()).isEqualTo(2);
	}
	
	@Test
	void mergeMetadata() {
		JsonWrapper data = new JsonWrapper().put("name", "value").putMetadata("meta1", "data1");
		JsonWrapper metadata = new JsonWrapper().putMetadata("meta2", "data2");

		assertWithMessage("expect metadata size to be 1")
			.that(data.getMetadata().size()).isEqualTo(1);
		
		data.mergeMetadata(metadata, "merge label");
 		
		int mergeSize = data.getMetadata().size();
		assertWithMessage("expect metadata size to be 2")
			.that(mergeSize).isEqualTo(2);
		
		String mergeLabel = data.getMetadata().get(1).getString(JsonWrapper.ENCODING_KEY);
		assertWithMessage("expect encodeLabel to be set")
			.that(mergeLabel).isEqualTo("merge label");
	}
	
	@Test
	void addMetadata_list() {
		JsonWrapper otherWrapper = new JsonWrapper().putMetadata("meta1", "data1");
		JsonWrapper metadata = new JsonWrapper().putMetadata("meta2", "data2");
		otherWrapper.addMetadata(metadata); // this makes a metadata list on the wrapper

		JsonWrapper dataWithMetadata = new JsonWrapper().put("name", "value").putMetadata("meta2", "data2");

		dataWithMetadata.mergeMetadata(otherWrapper, "merge label");
		
		int mergeSize = dataWithMetadata.getMetadata().size();
		assertWithMessage("expect metadata size to be 3")
			.that(mergeSize).isEqualTo(3);
		
		String mergeLabel = dataWithMetadata.getMetadata().get(1).getString(JsonWrapper.ENCODING_KEY);
		assertWithMessage("expect encodeLabel to be set")
			.that(mergeLabel).isEqualTo("merge label");
		
		mergeLabel = dataWithMetadata.getMetadata().get(2).getString(JsonWrapper.ENCODING_KEY);
		assertWithMessage("expect encodeLabel to be set")
			.that(mergeLabel).isEqualTo("merge label");
	}
	
	@Test
	void size() {
		JsonWrapper nullValue = new JsonWrapper((String)null);
		JsonWrapper value = new JsonWrapper("value");
		JsonWrapper map = new JsonWrapper().put("name", "value").put("name1", "value1");
		JsonWrapper list = new JsonWrapper().put("value1").put("value2").put("value3");

		assertWithMessage("expect metadata size to be 0")
			.that(nullValue.size()).isEqualTo(0);
		assertWithMessage("expect metadata size to be 1")
			.that(value.size()).isEqualTo(1);
		assertWithMessage("expect metadata size to be 2")
			.that(map.size()).isEqualTo(2);
		assertWithMessage("expect metadata size to be 3")
			.that(list.size()).isEqualTo(3);
	}
	
	@Test
	void jsonPathTesting_parsingAndBackToString() {
		String path = "$['a']['b'][1]['c']";
		
		JsonPath jp1 = JsonPath.compile(path);
		assertWithMessage("Path compiles bracket notation and returns the same.")
			.that(jp1.getPath()).isEqualTo(path);
		
		JsonPath jp2 = JsonPath.compile("$.a.b[1].c");
		assertWithMessage("Path.toString() generates the bracket notation while accepting dot notation.")
			.that(jp2.getPath()).isEqualTo(path);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void getByJsonPath_JsonPath() {
		String testPath = "$.a.b[0].c";
		JsonPath jsonPath = JsonPath.compile(testPath);
		List<String>[] paths = new List[1];
		
		JsonWrapper mockWrapper = new JsonWrapper() {
			@Override
			public JsonWrapper getByJsonPath(List<String> jsonPath) {
				paths[0] = jsonPath;
				return null;
			}
		};
		
		mockWrapper.get(jsonPath);
		
		// expect the same order as the path notation
		Truth.assertThat(paths[0].get(0)).isEqualTo("a");
		Truth.assertThat(paths[0].get(1)).isEqualTo("b");
		Truth.assertThat(paths[0].get(2)).isEqualTo("[0");
		Truth.assertThat(paths[0].get(3)).isEqualTo("c");
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void getByJsonPath_stringDotPath() {
		String dotPath = "$.a.b[0].c";
		List<String>[] paths = new List[1];
		
		JsonWrapper mockWrapper = new JsonWrapper() {
			@Override
			public JsonWrapper getByJsonPath(List<String> jsonPath) {
				paths[0] = jsonPath;
				return null;
			}
		};
		
		mockWrapper.getByJsonPath(dotPath);
		
		// expect the same order as the path notation
		Truth.assertThat(paths[0].get(0)).isEqualTo("a");
		Truth.assertThat(paths[0].get(1)).isEqualTo("b");
		Truth.assertThat(paths[0].get(2)).isEqualTo("[0");
		Truth.assertThat(paths[0].get(3)).isEqualTo("c");
	}
	
	@Test
	void getByJsonPath_List() {
		String value = "value";
		JsonWrapper hasC = new JsonWrapper().put("c", value);
		JsonWrapper list = new JsonWrapper().put(hasC);
		JsonWrapper hasB = new JsonWrapper().put("b", list);
		JsonWrapper hasA = new JsonWrapper().put("a", hasB);
		
		String testPath = "$.a.b[0].c";
		JsonPath jsonPath = JsonPath.compile(testPath);
		
		JsonWrapper actual =  hasA.get(jsonPath);

		assertWithMessage("Should follow the sole path in the wrapper.")
			.that(actual.toObject()).isEqualTo(value);
	}
	
	@Test
	void getByJsonPath_List2() {
		String value = "value";
		JsonWrapper hasC = new JsonWrapper().put("c", value).put("other","data").put("number",7);
		JsonWrapper list = new JsonWrapper().put("zeroth").put(hasC).put("last");
		JsonWrapper hasB = new JsonWrapper().put("b", list).put("more","map").put("float",6.6f);
		JsonWrapper hasA = new JsonWrapper().put("a", hasB).put("meep","mawp").put("bool",true);
		
		String testPath = "$.a.b[1].c";
		JsonPath jsonPath = JsonPath.compile(testPath);
		
		JsonWrapper actual =  hasA.get(jsonPath);

		assertWithMessage("Should return the 'c' entry from the second list entry of 'b'")
			.that(actual.toObject()).isEqualTo(value);
	}
	
	@Test
	void getByJsonPath_wildcard() {
		JsonWrapper actual = listStrWrapper.get((JsonPath)null);
		Truth.assertThat(actual).isNull();
		
		Truth.assertThat(
			assertThrows(UnsupportedOperationException.class, ()->{
				listStrWrapper.get(JsonPath.compile("$.a.b[*]"));
			}).getMessage() )
		.contains("Only definite");
	}
	
	@Test
	void getByJsonPath_returnNull() {
		String value = "value";
		JsonWrapper hasC = new JsonWrapper().put("c", value).put("other","data").put("number",7);
		JsonWrapper list = new JsonWrapper().put("zeroth").put(hasC).put("last");
		JsonWrapper hasB = new JsonWrapper().put("b", list).put("more","map").put("float",6.6f);
		JsonWrapper hasA = new JsonWrapper().put("a", hasB).put("meep","mawp").put("bool",true);
		
		String testPath = "$.a.b[12].c";
		JsonPath jsonPath = JsonPath.compile(testPath);
		
		JsonWrapper actual =  hasA.get(jsonPath);

		assertWithMessage("Expect null rather than an exception when path is not found.")
			.that(actual).isNull();
	}
	
}
