package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.util.JsonHelper;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.joda.time.Instant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JsonWrapperTest {

	private ObjectWriter ow = JsonWrapper.getObjectWriter(true);
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
		unfilteredMetaWrapper = new JsonWrapper(false);
	}

	@Test
	void testCopyConstructor() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putString("you're an array");
		JsonWrapper copyWrapper = new JsonWrapper(wrapper);

		assertThat(copyWrapper.toString()).isEqualTo(wrapper.toString());
	}

	@Test
	void testInitAsList() {
		assertWithMessage("Object should be null until the first put").
				that(listStrWrapper.getObject()).isNull();
		listStrWrapper.putString("name");
		Object list1 = listStrWrapper.getObject();
		assertThat(list1).isInstanceOf(List.class);
		listStrWrapper.putString("value");
		Object list2 = listStrWrapper.getObject();
		assertWithMessage("The internal instance should not change upon addition put")
				.that(list1)
				.isEqualTo(list2);
	}

	@Test
	void testInitAsObject() {
		assertWithMessage("Object should be null until the first put")
				.that(objectStrWrapper.getObject()).isNull();
		objectStrWrapper.putString("name", "value");
		Object obj1 = objectStrWrapper.getObject();
		assertThat(obj1).isInstanceOf(Map.class);
		objectStrWrapper.putString("name", "value");
		Object obj2 = objectStrWrapper.getObject();
		assertWithMessage("The internal instance should not change upon addition put")
				.that(obj1)
				.isEqualTo(obj2);
	}

	@Test
	void testGetObject_map() {
		objectStrWrapper.putString("name1", "value");
		assertThat(objectStrWrapper.getObject())
				.isInstanceOf(Map.class);
		assertThat(objectStrWrapper.getString("name1"))
				.isEqualTo("value");

		Object obj = new Object();
		objectObjWrapper.putObject("name2", obj);
		assertThat(objectObjWrapper.getObject())
				.isInstanceOf(Map.class);
		assertThat(obj)
				.isEqualTo(((Map<?,?>)objectObjWrapper.getObject()).get("name2"));
	}	

	@Test
	void testGetObject_list() {
		listStrWrapper.putString("name");
		assertThat(listStrWrapper.getObject()).isInstanceOf(List.class);
		assertThat((List<?>)listStrWrapper.getObject())
				.contains("name");
		Object obj = new Object();
		listObjWrapper.putObject(obj);
		assertThat(listObjWrapper.getObject())
				.isInstanceOf(List.class);
		assertThat(((List<?>)listObjWrapper.getObject()))
				.contains(obj);
	}	

	@Test
	void testIsObject_true() {
		assertWithMessage("should not be an object container until first put")
				.that(objectStrWrapper.isObject()).isFalse();
		objectStrWrapper.putString("name", "value");
		assertWithMessage("should be an object container after first put")
				.that(objectStrWrapper.isObject()).isTrue();
		assertWithMessage("should not be an object container until first map put")
				.that(objectObjWrapper.isObject()).isFalse();
		objectObjWrapper.putObject("name", new Object());
		assertWithMessage("should be an object container after first map put")
				.that(objectObjWrapper.isObject()).isTrue();
	}

	@Test
	void testIsObject_false() {
		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isObject())
				.isFalse();
		listStrWrapper.putString("name");
		assertWithMessage("should not be an object container after first list put")
				.that(listStrWrapper.isObject())
				.isFalse();

		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isObject())
				.isFalse();
		listObjWrapper.putObject(new Object());
		assertWithMessage("should not be an object container after first list put")
				.that(listStrWrapper.isObject())
				.isFalse();
	}

	@Test
	void testValidDateYyyyMmDd() throws Exception {
		objectObjWrapper.putDate("19690720");
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	void testValidDateYyyySlashMmSlashDd() throws Exception {
		objectObjWrapper.putDate("1969/07/20");
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	void testValidDateYyyyDashMmDashDd() throws Exception {
		objectObjWrapper.putDate("1969-07-20");
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	void testValidDateFromInstant() throws Exception {
		objectObjWrapper.putDate(Instant.now().toString());
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	void testValidDateFromLocalDate() throws Exception {
		objectObjWrapper.putDate(LocalDate.now().toString());
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	void testValidDateFromLocalDateTime() throws Exception {
		objectObjWrapper.putDate(LocalDateTime.now().toString());
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	void testValidDateYyMmDdIsInvalid() throws Exception {
		Assertions.assertThrows(EncodeException.class, () -> objectObjWrapper.putDate("690720"));
	}

	@Test
	void testNullObjectPut() {
		listStrWrapper.putObject( null );
		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isObject()).isFalse();
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
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putString("name2", "value2");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : \"value2\"\n}";
		assertWithMessage("expect a simple object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_integerArray() throws Exception {
		objectStrWrapper.putString("value1");
		objectStrWrapper.putInteger("100");

		String json = objectStrWrapper.toString();

		String expect = "[ \"value1\", 100 ]";
		assertWithMessage("expect a integer array of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_floatArray() throws Exception {
		objectStrWrapper.putString("value1");
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
	void testToString_exception() {
		objectObjWrapper.putObject("name", new MockBadJsonTarget());
		assertThrows(RuntimeException.class, () -> objectObjWrapper.toString());
	}

	@Test
	void testCheckState_objectThenList() {
		objectStrWrapper.putString("name", "value");
		assertThrows(IllegalStateException.class, () -> objectStrWrapper.putString("value"));
	}

	@Test
	void testCheckState_listThenLObject() {
		listStrWrapper.putString("value");
		assertThrows(IllegalStateException.class,
				() -> listStrWrapper.putString("name", "value"));
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
	void testCleanString_null() throws Exception {
		assertThat(objectStrWrapper.cleanString(null)).isNotNull();
	}

	@Test
	void testToString_booleanArray() throws Exception {
		objectStrWrapper.putString("True"); // as string where case is preserved
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
		objectObjWrapper.putString("name1", "value1");
		objectObjWrapper.putObject("name2", new String[] {"A","B","C"});
		objectObjWrapper.putString("name3", "value3");

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
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putInteger("name2", "100");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 100\n}";
		assertWithMessage("expect a integer object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_floatObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putFloat("name2", "1.01");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 1.01\n}";
		assertWithMessage("expect a float value object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_booleanObject() throws Exception {
		objectStrWrapper.putString("name", "True"); // as string where case is preserved
		objectStrWrapper.putBoolean("name1", "True");
		objectStrWrapper.putBoolean("name2", "TRUE");
		objectStrWrapper.putBoolean("name3", "true");
		objectStrWrapper.putBoolean("name4", "trUe");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name\" : \"True\",\n  \"name1\" : true,\n  \"name2\" : true,\n  \"name3\" : true,\n  \"name4\" : true\n}";
		assertThat(json)
				.isEqualTo(expect);
	}

	@Test
	void testStripWrapper_list() {
		listStrWrapper.putString("A");
		listStrWrapper.putString("B");
		listStrWrapper.putString("C");

		Object result = objectObjWrapper.stripWrapper(listStrWrapper);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(List.class);
	}

	@Test
	void testStripWrapper_object() {
		objectStrWrapper.putString("obj1", "A");
		objectStrWrapper.putString("obj2", "B");

		Object result = objectObjWrapper.stripWrapper(objectStrWrapper);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Map.class);
	}

	@Test
	void testToString_objectWithList() throws Exception {
		listStrWrapper.putString("A");
		listStrWrapper.putString("B");
		listStrWrapper.putString("C");

		objectObjWrapper.putString("name1", "value1");
		objectObjWrapper.putObject("name2", listStrWrapper);
		objectObjWrapper.putString("name3", "value3");

		String json = objectObjWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n"+
				"  \"name2\" : [ \"A\", \"B\", \"C\" ],\n" +
				"  \"name3\" : \"value3\"\n}";
		assertWithMessage("expect list to look like array")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	void testToString_objectWithChild() throws Exception {
		objectStrWrapper.putString("obj1", "A");
		objectStrWrapper.putString("obj2", "B");

		objectObjWrapper.putString("name1", "value1");
		objectObjWrapper.putObject("name2", objectStrWrapper);
		objectObjWrapper.putString("name3", "value3");

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
		objectStrWrapper.putString("obj1", "A");
		objectStrWrapper.putString("obj2", "B");

		objectObjWrapper.putString("name1", "value1");
		objectObjWrapper.putString("name3", "value3");
		objectObjWrapper.putObject("name2", objectStrWrapper);

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
		objectStrWrapper.putString("obj1", "A");
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
		String shouldNotSerialize = "metadata_meep";
		objectObjWrapper.putString(shouldSerialize, shouldSerialize);
		objectObjWrapper.putString(shouldNotSerialize, shouldNotSerialize);

		//when
		String json = objectObjWrapper.toString();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode obj = mapper.readTree(json);

		//then
		assertThat(shouldSerialize)
				.isEqualTo(obj.findValue(shouldSerialize).asText());
		assertThat(obj.findValue(shouldNotSerialize))
				.isNull();
	}

	@Test
	void metadataUnfiltered() throws IOException {
		//setup
		String shouldSerialize = "mawp";
		String shouldAlsoSerialize = "metadata_meep";
		unfilteredMetaWrapper.putString(shouldSerialize, shouldSerialize);
		unfilteredMetaWrapper.putString(shouldAlsoSerialize, shouldAlsoSerialize);

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
	@SuppressWarnings("unchecked")
	void testContentStream() {
		objectObjWrapper.putString("meep", "mawp");
		InputStream content = objectObjWrapper.toSource().toInputStream();
		Map<String, String> contentMap = JsonHelper.readJson(content, Map.class);
		assertThat(contentMap.get("meep")).isEqualTo("mawp");
	}

	@Test
	void testObjectStream() {
		assertThat(objectObjWrapper.stream().count())
				.isEqualTo(1);
	}

	@Test
	void testListStreamNonMap() {
		JsonWrapper listWrapper = new JsonWrapper();
		listWrapper.putObject(new Object());
		assertThat(listWrapper.stream().count())
				.isEqualTo(0);
	}

	@Test
	void testListStreamMap() {
		JsonWrapper listWrapper = new JsonWrapper();
		listWrapper.putObject(new HashMap());
		listWrapper.putObject(new HashMap());
		assertThat(listWrapper.stream().count())
				.isEqualTo(2);
	}

}

class MockBadJsonTarget {
	@SuppressWarnings("unused")
	private String getVar() {
		return "";
	}
}
