package gov.cms.qpp.conversion.encode;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;

public class JsonWrapperTest {

	private ObjectWriter ow = JsonWrapper.getObjectWriter(true);
	private JsonWrapper objectObjWrapper;
	private JsonWrapper objectStrWrapper;
	private JsonWrapper listObjWrapper;
	private JsonWrapper listStrWrapper;
	private JsonWrapper unfilteredMetaWrapper;

	@Before
	public void before() {
		objectObjWrapper = new JsonWrapper();
		objectStrWrapper = new JsonWrapper();
		listObjWrapper   = new JsonWrapper();
		listStrWrapper   = new JsonWrapper();
		unfilteredMetaWrapper = new JsonWrapper(false);
	}

	@Test
	public void testCopyConstructor() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putString("you're an array");
		JsonWrapper copyWrapper = new JsonWrapper(wrapper);

		assertWithMessage("String representations should be equal")
				.that(copyWrapper.toString()).isEqualTo(wrapper.toString());
	}

	@Test
	public void testInitAsList() {
		assertWithMessage("Object should be null until the first put").
				that(listStrWrapper.getObject()).isNull();
		listStrWrapper.putString("name");
		Object list1 = listStrWrapper.getObject();
		assertWithMessage("Init should be as a list").that(list1).isNotNull();
		assertWithMessage("Init should be as a list").that(list1).isInstanceOf(List.class);
		listStrWrapper.putString("value");
		Object list2 = listStrWrapper.getObject();
		assertWithMessage("The internal instance should not change upon addition put")
				.that(list1)
				.isEqualTo(list2);
	}

	@Test
	public void testInitAsObject() {
		assertWithMessage("Object should be null until the first put")
				.that(objectStrWrapper.getObject()).isNull();
		objectStrWrapper.putString("name", "value");
		Object obj1 = objectStrWrapper.getObject();
		assertWithMessage("Init should be as a map").that(obj1).isNotNull();
		assertWithMessage("Init should be as a map").that(obj1).isInstanceOf(Map.class);
		objectStrWrapper.putString("name", "value");
		Object obj2 = objectStrWrapper.getObject();
		assertWithMessage("The internal instance should not change upon addition put")
				.that(obj1)
				.isEqualTo(obj2);
	}

	@Test
	public void testGetObject_map() {
		objectStrWrapper.putString("name1", "value");
		assertWithMessage("should be as a map")
				.that(objectStrWrapper.getObject())
				.isInstanceOf(Map.class);
		assertWithMessage("map should contain put value")
				.that(objectStrWrapper.getString("name1"))
				.isEqualTo("value");

		Object obj = new Object();
		objectObjWrapper.putObject("name2", obj);
		assertWithMessage("should be as a map")
				.that(objectObjWrapper.getObject())
				.isInstanceOf(Map.class);
		assertWithMessage("map should contain put value")
				.that(obj)
				.isEqualTo(((Map<?,?>)objectObjWrapper.getObject()).get("name2"));
	}	

	@Test
	public void testGetObject_list() {
		listStrWrapper.putString("name");
		assertWithMessage("should be as a list")
				.that(listStrWrapper.getObject()).isInstanceOf(List.class);
		assertWithMessage("")
				.that((List<?>)listStrWrapper.getObject())
				.contains("name");
		Object obj = new Object();
		listObjWrapper.putObject(obj);
		assertWithMessage("should be as a list")
				.that(listObjWrapper.getObject())
				.isInstanceOf(List.class);
		assertWithMessage("list should contain put value")
				.that(((List<?>)listObjWrapper.getObject()))
				.contains(obj);
	}	

	@Test
	public void testIsObject_true() {
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
	public void testIsObject_false() {
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
	public void testValidDate() throws Exception {
		objectObjWrapper.putDate("19690720");
		assertWithMessage("should be an object container")
				.that(((List<?>) objectObjWrapper.getObject()))
				.isNotEmpty();
	}

	@Test
	public void testNullObjectPut() {
		listStrWrapper.putObject( null );
		assertWithMessage("should not be an object container")
				.that(listStrWrapper.isObject()).isFalse();
	}

	@Test
	public void testJackson_simpleObject() throws Exception {
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
	public void testJackson_objectWithArray() throws Exception {
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
	public void testJackson_objectWithList() throws Exception {
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
	public void testJackson_objectWithChild() throws Exception {
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
	public void testJackson_objectWithChild_commaAndOrder() throws Exception {
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
	public void testToString_simpleObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putString("name2", "value2");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : \"value2\"\n}";
		assertWithMessage("expect a simple object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	public void testToString_integerArray() throws Exception {
		objectStrWrapper.putString("value1");
		objectStrWrapper.putInteger("100");

		String json = objectStrWrapper.toString();

		String expect = "[ \"value1\", 100 ]";
		assertWithMessage("expect a integer array of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	public void testToString_floatArray() throws Exception {
		objectStrWrapper.putString("value1");
		objectStrWrapper.putFloat("1.01");

		String json = objectStrWrapper.toString();

		String expect = "[ \"value1\", 1.01 ]";
		assertWithMessage("expect a float value array of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test(expected=EncodeException.class)
	public void testBadKeyedPutDate_exception() throws Exception {
		objectObjWrapper.putDate("A date which will live in infamy", "December 7, 1941");
		fail("should not get here, expecting runtime encode exception");
	}

	@Test(expected=EncodeException.class)
	public void testBadPutDate_exception() throws Exception {
		objectObjWrapper.putDate("December 7, 1941");
		fail("should not get here, expecting runtime encode exception");
	}

	@Test(expected=RuntimeException.class)
	public void testToString_exception() {
		objectObjWrapper.putObject("name", new MockBadJsonTarget());
		objectObjWrapper.toString();
		fail("should not get here, expecting runtime parse exception");
	}

	@Test(expected=IllegalStateException.class)
	public void testCheckState_objectThenList() {
		objectStrWrapper.putString("name", "value");
		objectStrWrapper.putString("value");
		fail("should not make it here, prior line should throw exception");
	}

	@Test(expected=IllegalStateException.class)
	public void testCheckState_listThenLObject() {
		listStrWrapper.putString("value");
		listStrWrapper.putString("name", "value");
		fail("should not make it here, prior line should throw exception");
	}

	@Test(expected=EncodeException.class)
	public void testToString_integerParseExcpetion() throws Exception {
		objectStrWrapper.putInteger("name2", "nope");
		fail("Should not make it here because of integer parse exception");
	}

	@Test(expected=EncodeException.class)
	public void testToString_integerParseExcpetion2() throws Exception {
		objectStrWrapper.putInteger("nope");
		fail("Should not make it here because of integer parse exception");
	}

	@Test(expected=EncodeException.class)
	public void testToString_floatParseExcpetion() throws Exception {
		objectStrWrapper.putFloat("nope");
		fail("Should not make it here because of float parse exception");
	}

	@Test(expected=EncodeException.class)
	public void testToString_floatParseExcpetion2() throws Exception {
		objectStrWrapper.putFloat("name","nope");
		fail("Should not make it here because of float parse exception");
	}

	@Test(expected=EncodeException.class)
	public void testToString_booleanParseExcpetion() throws Exception {
		objectStrWrapper.putBoolean("nope");
		fail("Should not make it here because of float parse exception");
	}

	@Test(expected=EncodeException.class)
	public void testToString_booleanParseExcpetion2() throws Exception {
		objectStrWrapper.putBoolean("name","nope");
		fail("Should not make it here because of float parse exception");
	}

	@Test
	public void testValidBoolean() throws Exception {
		assertThat(objectStrWrapper.validBoolean("True")).isTrue();
		assertThat(objectStrWrapper.validBoolean("Yes")).isTrue();
		assertThat(objectStrWrapper.validBoolean("Y")).isTrue();
		assertThat(objectStrWrapper.validBoolean("false")).isFalse();
		assertThat(objectStrWrapper.validBoolean("no")).isFalse();
		assertThat(objectStrWrapper.validBoolean("N")).isFalse();
	}

	@Test
	public void testCleanString_null() throws Exception {
		assertThat(objectStrWrapper.cleanString(null)).isNotNull();
	}

	@Test
	public void testToString_booleanArray() throws Exception {
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
	public void testToString_objectWithArray() throws Exception {
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
	public void testToString_integerObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putInteger("name2", "100");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 100\n}";
		assertWithMessage("expect a integer object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	public void testToString_floatObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putFloat("name2", "1.01");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 1.01\n}";
		assertWithMessage("expect a float value object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	public void testToString_booleanObject() throws Exception {
		objectStrWrapper.putString("name", "True"); // as string where case is preserved
		objectStrWrapper.putBoolean("name1", "True");
		objectStrWrapper.putBoolean("name2", "TRUE");
		objectStrWrapper.putBoolean("name3", "true");
		objectStrWrapper.putBoolean("name4", "trUe");

		String json = objectStrWrapper.toString();

		String expect = "{\n  \"name\" : \"True\",\n  \"name1\" : true,\n  \"name2\" : true,\n  \"name3\" : true,\n  \"name4\" : true\n}";
		assertWithMessage("expect a boolean value object of JSON")
				.that(json)
				.isEqualTo(expect);
	}

	@Test
	public void testStripWrapper_list() {
		listStrWrapper.putString("A");
		listStrWrapper.putString("B");
		listStrWrapper.putString("C");

		Object result = objectObjWrapper.stripWrapper(listStrWrapper);

		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(List.class);
	}

	@Test
	public void testStripWrapper_object() {
		objectStrWrapper.putString("obj1", "A");
		objectStrWrapper.putString("obj2", "B");

		Object result = objectObjWrapper.stripWrapper(objectStrWrapper);
		assertThat(result).isNotNull();
		assertThat(result).isInstanceOf(Map.class);
	}

	@Test
	public void testToString_objectWithList() throws Exception {
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
	public void testToString_objectWithChild() throws Exception {
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
	public void testToString_objectWithChild_commaAndOrder() throws Exception {
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
	public void testValueRetrieval() throws Exception {
		objectStrWrapper.putString("obj1", "A");
		objectStrWrapper.putInteger("obj2", "1");
		objectStrWrapper.putFloat("obj3", "1.1");
		objectStrWrapper.putBoolean("obj4", "false");

		assertWithMessage("expect String")
				.that(objectStrWrapper.getString("obj1"))
				.isEqualTo("A");
		assertWithMessage("expect Integer")
				.that(objectStrWrapper.getInteger("obj2"))
				.isEqualTo(1);
		assertWithMessage("expect Float")
				.that(objectStrWrapper.getFloat("obj3"))
				.isEqualTo(1.1F);
		assertWithMessage("expect Boolean")
				.that(objectStrWrapper.getBoolean("obj4"))
				.isFalse();
	}

	@Test
	public void metadataFiltered() throws IOException {
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
		assertWithMessage("Could not find %s", shouldSerialize)
				.that(shouldSerialize)
				.isEqualTo(obj.findValue(shouldSerialize).asText());
		assertWithMessage("Should not find %s", shouldNotSerialize)
				.that(obj.findValue(shouldNotSerialize))
				.isNull();
	}

	@Test
	public void metadataUnfiltered() throws IOException {
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
		assertWithMessage("Could not find %s", shouldSerialize)
				.that(shouldSerialize)
				.isEqualTo(obj.findValue(shouldSerialize).asText());
		assertWithMessage("Could not find %s", shouldAlsoSerialize)
				.that(shouldAlsoSerialize)
				.isEqualTo(obj.findValue(shouldAlsoSerialize).asText());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testContentStream() {
		objectObjWrapper.putString("meep", "mawp");
		InputStream content = objectObjWrapper.contentStream();
		Map<String, String> contentMap = JsonHelper.readJson(content, Map.class);
		assertThat(contentMap.get("meep")).isEqualTo("mawp");
	}

}

class MockBadJsonTarget {
	@SuppressWarnings("unused")
	private String getVar() {
		return "";
	}
}
