package gov.cms.qpp.conversion.util;

import java.util.Properties;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
		Assert.assertFalse(EnvironmentHelper.isPresent(random));
	}

	@Test
	public void testIsPresentOnAdded() {
		String someKey = UUID.randomUUID().toString();
		System.setProperty(someKey, "nothing important");
		Assert.assertTrue(EnvironmentHelper.isPresent(someKey));
	}
}
