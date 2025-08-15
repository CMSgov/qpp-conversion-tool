package gov.cms.qpp.conversion.util;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reflections.util.ClasspathHelper;

import com.jayway.jsonpath.TypeRef;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.test.helper.HelperContract;

class JsonHelperTest implements HelperContract {

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
		configurations = JsonHelper.readJsonAtJsonPath(measuresInput, "$", new TypeRef<List<MeasureConfig>>() { });

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
					.that(exception).hasMessageThat().isSameInstanceAs("Problem parsing json string");
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
					.that(exception).hasMessageThat().isSameInstanceAs("Problem parsing json string");
		} catch(Exception exception) {
			Assertions.fail("Incorrect exception was thrown.");
		}
	}

	@Test
	void readJsonFromPathWithClass() throws Exception {
		// create temp file
		Path tempFile = Files.createTempFile("jsonhelper-test", ".json");
		Files.writeString(tempFile, "{\"key\":\"value\"}");

		Map<String, String> result = JsonHelper.readJson(tempFile, Map.class);
		assertWithMessage("Expect to read key from file").that(result.get("key")).isEqualTo("value");

		Files.deleteIfExists(tempFile);
	}

	@Test
	void readJsonFromInputStreamWithTypeReference() {
		String json = "[{\"key\":\"value\"}]";
		InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

		List<Map<String, String>> result = JsonHelper.readJson(is, new TypeReference<List<Map<String, String>>>() {});
		assertWithMessage("Expect first element to have key=value").that(result.get(0).get("key")).isEqualTo("value");
	}

	@Test
	void readJsonFromPathWithTypeReference() throws Exception {
		Path tempFile = Files.createTempFile("jsonhelper-test-typeRef", ".json");
		Files.writeString(tempFile, "[{\"key\":\"value\"}]");

		List<Map<String, String>> result = JsonHelper.readJson(tempFile, new TypeReference<List<Map<String, String>>>() {});
		assertWithMessage("Expect first element to have key=value").that(result.get(0).get("key")).isEqualTo("value");

		Files.deleteIfExists(tempFile);
	}

	@Test
	void readJsonAtJsonPathFromString() {
		String json = "{ \"measurements\": { \"measures\": [" +
				"{\"measureId\":\"ACI_INFBLO_1\", \"value\":100}," +
				"{\"measureId\":\"ACI_INFBLO_2\", \"value\":80}" +
				"] } }";

		List<Map<String, Object>> measures = JsonHelper.readJsonAtJsonPath(
				json,
				"$.measurements.measures[*]",
				new TypeRef<List<Map<String, Object>>>() {}
		);

		assertWithMessage("Should contain 2 measures").that(measures).hasSize(2);
		assertWithMessage("First measure ID").that(measures.get(0).get("measureId")).isEqualTo("ACI_INFBLO_1");
	}

	@Override
	public Class<?> getHelperClass() {
		return JsonHelper.class;
	}
}