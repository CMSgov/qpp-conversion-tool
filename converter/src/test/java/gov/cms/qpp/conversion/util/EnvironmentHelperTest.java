package gov.cms.qpp.conversion.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.util.UUID;

import static com.google.common.truth.Truth.assertWithMessage;

public class EnvironmentHelperTest {

	private static Properties properties;

	@BeforeClass
	public static void saveProperties() {
		properties = new Properties(System.getProperties());
	}

	@AfterClass
	public static void teardown() {
		System.setProperties(properties);
	}

	@Test
	public void testIsPresentOnRandomString() {
		String random = UUID.randomUUID().toString();
		assertWithMessage("Should not have an environment variable with randomized key")
				.that(EnvironmentHelper.isPresent(random)).isFalse();
	}

	@Test
	public void testIsPresentOnAdded() {
		String someKey = UUID.randomUUID().toString();
		System.setProperty(someKey, "nothing important");
		assertWithMessage("%s should be set to %s", someKey, "nothing important")
				.that(EnvironmentHelper.isPresent(someKey)).isTrue();
	}
}
