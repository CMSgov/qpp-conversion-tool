package gov.cms.qpp.conversion.util;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;

/**
 * Test class to increase JaCoCo code coverage
 */
class JsonHelperTest {

	@Test
	void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<JsonHelper> constructor = JsonHelper.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		JsonHelper jsonHelper = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);

		assertWithMessage("Expect to have an instance here ")
				.that(jsonHelper).isInstanceOf(JsonHelper.class);
	}

	@Test
	void readJsonAtJsonPath() throws Exception {
		String measureDataFileName = "measures-data.json";
		List<MeasureConfig> configurations;
		InputStream measuresInput = ClasspathHelper.contextClassLoader().getResourceAsStream(measureDataFileName);
		configurations = JsonHelper.readJsonAtJsonPath(measuresInput, "$", List.class);

		assertWithMessage("Expect to get a List of measureConfigs")
				.that(configurations).isNotEmpty();
	}

	@Test
	void exceptionForReadJsonInputStream() {
		String testJson = "{ \"DogCow\": [ }";

		try {
			JsonHelper.readJson(new ByteArrayInputStream(testJson.getBytes()), Map.class);
			Assertions.fail("An exception should have been thrown.");
		} catch(JsonReadException exception) {
			assertWithMessage("Wrong exception reason.")
					.that(exception).hasMessageThat().isSameAs("Problem parsing json string");
		} catch(Exception exception) {
			Assertions.fail("Incorrect exception was thrown.");
		}
	}

	@Test
	void exceptionForReadJsonString() {
		String testJson = "{ \"DogCow\": [ }";

		try {
			JsonHelper.readJson(testJson, Map.class);
			Assertions.fail("An exception should have been thrown.");
		} catch(JsonReadException exception) {
			assertWithMessage("Wrong exception reason.")
					.that(exception).hasMessageThat().isSameAs("Problem parsing json string");
		} catch(Exception exception) {
			Assertions.fail("Incorrect exception was thrown.");
		}
	}
}