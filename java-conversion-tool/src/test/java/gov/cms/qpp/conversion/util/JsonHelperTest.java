package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Test class to increase JaCoCo code coverage
 */
public class JsonHelperTest {

	@Test
	public void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<JsonHelper> constructor = JsonHelper.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		JsonHelper jsonHelper = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);
		assertThat("Expect to have an instance here ", jsonHelper, instanceOf(JsonHelper.class));
	}

	@Test
	public void readJsonAtJsonPath() throws Exception {
		String measureDataFileName = "measures-data.json";
		List<MeasureConfig> configurations;
		InputStream measuresInput = ClasspathHelper.contextClassLoader().getResourceAsStream(measureDataFileName);
		configurations = JsonHelper.readJsonAtJsonPath(measuresInput, "$", List.class);
		assertThat("Expect to get a List of measureConfigs", configurations, is(not(empty())));
	}

	@Test
	public void exceptionForReadJsonInputStream() {
		String testJson = "{ \"DogCow\": [ }";

		try {
			JsonHelper.readJson(new ByteArrayInputStream(testJson.getBytes()), Map.class);
			fail("An exception should have been thrown.");
		} catch(JsonReadException exception) {
			assertThat("Wrong exception reason.", exception.getMessage(), is("Problem parsing json string"));
		} catch(Exception exception) {
			fail("Incorrect exception was thrown.");
		}
	}

	@Test
	public void exceptionForReadJsonString() {
		String testJson = "{ \"DogCow\": [ }";

		try {
			JsonHelper.readJson(testJson, Map.class);
			fail("An exception should have been thrown.");
		} catch(JsonReadException exception) {
			assertThat("Wrong exception reason.", exception.getMessage(), is("Problem parsing json string"));
		} catch(Exception exception) {
			fail("Incorrect exception was thrown.");
		}
	}
}