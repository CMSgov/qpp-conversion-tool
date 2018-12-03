package gov.cms.qpp.conversion.util;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Properties;
import java.util.UUID;

import gov.cms.qpp.test.logging.LoggerContract;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnvironmentHelperTest implements LoggerContract {

	private static Properties properties;

	@BeforeAll
	static void saveProperties() {
		properties = new Properties(System.getProperties());
	}

	@AfterAll
	static void teardown() {
		System.setProperties(properties);
	}

	@Test
	void testIsPresentOnRandomString() {
		String random = UUID.randomUUID().toString();

		assertWithMessage("Should not have an environment variable with randomized key")
				.that(EnvironmentHelper.isPresent(random)).isFalse();
	}

	@Test
	void testLogEntryForFailures() {
		String random = UUID.randomUUID().toString();
		String message = String.format(EnvironmentHelper.NOT_FOUND, random);
		EnvironmentHelper.get(random);

		assertThat(getLogs()).contains(message);
	}

	@Test
	void testIsPresentOnAdded() {
		String someKey = UUID.randomUUID().toString();
		String value = "nothing important";
		System.setProperty(someKey, value);
		assertWithMessage("%s should be set to %s", someKey, value)
				.that(EnvironmentHelper.isPresent(someKey)).isTrue();
	}

	@Test
	void testIsPresentButEmpty() {
		String someKey = UUID.randomUUID().toString();
		String value = "";
		System.setProperty(someKey, value);
		assertWithMessage("%s should be set to %s", someKey, value)
			.that(EnvironmentHelper.isPresent(someKey)).isFalse();
	}

	@Test
	void testLogEntryForIsPresentFailureIsEmpty() {
		String someKey = UUID.randomUUID().toString();
		String value = "";
		System.setProperty(someKey, value);
		String message = String.format(EnvironmentHelper.NOT_FOUND, someKey);
		EnvironmentHelper.isPresent(someKey);

		assertThat(getLogs()).doesNotContain(message);
	}

	@Override
	public Class<?> getLoggerType() {
		return EnvironmentHelper.class;
	}
}
