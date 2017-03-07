package gov.cms.qpp.conversion.encode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonWrapperTest {

	ObjectWriter ow = JsonWrapper.getObjectWriter();
	
	public JsonWrapper objectObjWrapper;
	public JsonWrapper objectStrWrapper;
	public JsonWrapper listObjWrapper;
	public JsonWrapper listStrWrapper;
	
	@Before
	public void before() {
		objectObjWrapper = new JsonWrapper();
		objectStrWrapper = new JsonWrapper();
		listObjWrapper   = new JsonWrapper();
		listStrWrapper   = new JsonWrapper();
	}

	
	
	@Test
	public void testInitAsList() {
		assertTrue("Object should be null until the first put", listStrWrapper.getObject() == null);
		listStrWrapper.putString("name");
		Object list1 = listStrWrapper.getObject();
		assertNotNull("Init should be as a list", list1);
		assertTrue("Init should be as a list", list1 instanceof List);
		listStrWrapper.putString("value");
		Object list2 = listStrWrapper.getObject();
		assertEquals("The internal instance should not change upon addition put", list1,list2);
	}
	
	
	@Test
	public void testInitAsObject() {
		assertTrue("Object should be null until the first put", objectStrWrapper.getObject() == null);
		objectStrWrapper.putString("name", "value");
		Object obj1 = objectStrWrapper.getObject();
		assertNotNull("Init should be as a map", obj1);
		assertTrue("Init should be as a map", obj1 instanceof Map);
		objectStrWrapper.putString("name", "value");
		Object obj2 = objectStrWrapper.getObject();
		assertEquals("The internal instance should not change upon addition put", obj1,obj2);
	}
	
	
	@Test
	public void testGetObject_map() {
		objectStrWrapper.putString("name1", "value");
		assertTrue("should be as a map", objectStrWrapper.getObject() instanceof Map);
		assertEquals("map should contain put value", 
				"value", objectStrWrapper.getString("name1"));
		
		Object obj = new Object();
		objectObjWrapper.putObject("name2", obj);
		assertTrue("should be as a map",  objectObjWrapper.getObject() instanceof Map);
		assertEquals("map should contain put value",
				obj, ((Map<?,?>)objectObjWrapper.getObject()).get("name2"));
	}	
	
	@Test
	public void testGetObject_list() {
		listStrWrapper.putString("name");
		assertTrue("should be as a list", listStrWrapper.getObject() instanceof List);
		assertTrue("lsit should contian put value",
				((List<?>)listStrWrapper.getObject()).contains("name"));
		
		Object obj = new Object();
		listObjWrapper.putObject(obj);
		assertTrue("should be as a list", listObjWrapper.getObject() instanceof List);
		assertTrue("lsit should contian put value",
				((List<?>)listObjWrapper.getObject()).contains(obj));
	}	
	
	@Test
	public void testIsObject_true() {
		assertFalse("should not be an object container until first put", objectStrWrapper.isObject());
		objectStrWrapper.putString("name", "value");
		assertTrue("should be an object container after first put", objectStrWrapper.isObject());
		
		assertFalse("should not be an object container until first map put", objectObjWrapper.isObject());
		objectObjWrapper.putObject("name", new Object());
		assertTrue("should be an object container after first map put", objectObjWrapper.isObject());
	}
	
	@Test
	public void testIsObject_false() {
		assertFalse("should not be an object container", listStrWrapper.isObject());
		listStrWrapper.putString("name");
		assertFalse("should not be an object container after first list put", listStrWrapper.isObject());
		
		assertFalse("should not be an object container", listObjWrapper.isObject());
		listObjWrapper.putObject(new Object());
		assertFalse("should not be an object container after first list put", listObjWrapper.isObject());
	}
	
	
	@Test
	public void testJackson_simpleObject() throws Exception {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("name1", "value1");
		map.put("name2", "value2");
		
		String json = ow.writeValueAsString(map);
		
		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : \"value2\"\n}";
		assertEquals("expect a simple object of JSON",
				expect, json);
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
		assertEquals("expect array to use [] rather than {} block",
				expect, json);
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
		assertEquals("expect list to look like array",
				expect, json);
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
		assertEquals("expect comma after child and no comma after last value pair",
				expect, json);
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
		assertEquals("expect no comma expected after the child and order as inserted",
				expect, json);
	}

	@Test
	public void testToString_simpleObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putString("name2", "value2");
		
		String json = objectStrWrapper.toString();
		
		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : \"value2\"\n}";
		assertEquals("expect a simple object of JSON",
				expect, json);
	}
	@Test
	public void testToString_integerArray() throws Exception {
		objectStrWrapper.putString("value1");
		objectStrWrapper.putInteger("100");
		
		String json = objectStrWrapper.toString();
		
		String expect = "[ \"value1\", 100 ]";
		assertEquals("expect a integer array of JSON",
				expect, json);
	}
	@Test
	public void testToString_floatArray() throws Exception {
		objectStrWrapper.putString("value1");
		objectStrWrapper.putFloat("1.01");
		
		String json = objectStrWrapper.toString();
		
		String expect = "[ \"value1\", 1.01 ]";
		assertEquals("expect a float value array of JSON",
				expect, json);
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
		assertTrue(objectStrWrapper.validBoolean("True"));
		assertTrue(objectStrWrapper.validBoolean("Yes"));
		assertTrue(objectStrWrapper.validBoolean("Y"));
		assertFalse(objectStrWrapper.validBoolean("false"));
		assertFalse(objectStrWrapper.validBoolean("no"));
		assertFalse(objectStrWrapper.validBoolean("N"));
	}
	@Test
	public void testCleanString_null() throws Exception {
		assertNotNull(objectStrWrapper.cleanString(null));
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
		assertEquals("expect a boolean value object of JSON",
				expect, json);
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
		assertEquals("expect array to use [] rather than {} block",
				expect, json);
	}

	@Test
	public void testToString_integerObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putInteger("name2", "100");
		
		String json = objectStrWrapper.toString();
		
		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 100\n}";
		assertEquals("expect a integer object of JSON",
				expect, json);
	}
	@Test
	public void testToString_floatObject() throws Exception {
		objectStrWrapper.putString("name1", "value1");
		objectStrWrapper.putFloat("name2", "1.01");
		
		String json = objectStrWrapper.toString();
		
		String expect = "{\n  \"name1\" : \"value1\",\n  \"name2\" : 1.01\n}";
		assertEquals("expect a float value object of JSON",
				expect, json);
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
		assertEquals("expect a boolean value object of JSON",
				expect, json);
	}
	
	@Test
	public void testStripWrapper_list() {
		listStrWrapper.putString("A");
		listStrWrapper.putString("B");
		listStrWrapper.putString("C");

		Object result = objectObjWrapper.stripWrapper(listStrWrapper);
		
		assertNotNull(result);
		assertTrue(result instanceof List<?>);
	}
	@Test
	public void testStripWrapper_object() {
		objectStrWrapper.putString("obj1", "A");
		objectStrWrapper.putString("obj2", "B");

		Object result = objectObjWrapper.stripWrapper(objectStrWrapper);
		assertNotNull(result);
		assertTrue(result instanceof Map<?,?>);
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
		assertEquals("expect list to look like array",
				expect, json);
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
		assertEquals("expect comma after child and no comma after last value pair",
				expect, json);
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
		assertEquals("expect no comma expected after the child and order as inserted",
				expect, json);
	}
	
}

class MockBadJsonTarget {
	private String var = "";
	@SuppressWarnings("unused")
	private String getVar() {
		return var;
	}
}
