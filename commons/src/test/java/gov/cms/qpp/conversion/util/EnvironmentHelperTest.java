package gov.cms.qpp.conversion.util;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Properties;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EnvironmentHelperTest {

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
	void testIsPresentOnAdded() {
		String someKey = UUID.randomUUID().toString();
		System.setProperty(someKey, "nothing important");
		assertWithMessage("%s should be set to %s", someKey, "nothing important")
				.that(EnvironmentHelper.isPresent(someKey)).isTrue();
	}
}
