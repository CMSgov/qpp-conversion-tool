package gov.cms.qpp.conversion.encode;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonWrapperTest {

	ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
	
	
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

}
