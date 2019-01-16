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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
				.that(objectStrWrapper.isObject()).isFalse();
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
				.that(objectStrWrapper.isObject()).isTrue();
//		objectObjWrapper.put("name", new Object()); TODO asdf
//		assertWithMessage("should be an object container after first map put")
//				.that(objectObjWrapper.isObject()).isTrue();
	}

	@Test
	void testIsObject_false() {
		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isObject())
				.isFalse();
		listStrWrapper.put("name");
		assertWithMessage("should not be an object container after first list put")
				.that(listStrWrapper.isObject())
				.isFalse();

		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isObject())
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

//	@Test TODO asdf
//	void testToString_exception() {
//		objectObjWrapper.put("name", new MockBadJsonTarget());
//		assertThrows(RuntimeException.class, () -> objectObjWrapper.toString());
//	}

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
		
		listObjWrapper.addMetaMap(objectObjWrapper);
		listObjWrapper.addMetaMap(objectStrWrapper);
		
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

//	@Test TODO asdf
//	void testListStreamNonMap() { 
//		JsonWrapper listWrapper = new JsonWrapper();
//		listWrapper.put(new Object());
//		assertThat(listWrapper.stream().count())
//				.isEqualTo(0);
//	}

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
}

class MockBadJsonTarget {
	@SuppressWarnings("unused")
	private String getVar() {
		return "";
	}
}
