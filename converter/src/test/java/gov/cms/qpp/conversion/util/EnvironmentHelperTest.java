package gov.cms.qpp.conversion.util;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

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

	@Test
	public void testValueForPresent() {
		String someKey = UUID.randomUUID().toString();
		String someValue = "DogCow";
		System.setProperty(someKey, someValue);
		assertThat("The value for the variable is incorrect.", EnvironmentHelper.valueFor(someKey), is(someValue));
	}

	@Test
	public void testValueForMotPresent() {
		String someKey = UUID.randomUUID().toString();
		String someValue = "Moof";
		assertThat("The value for the variable is incorrect.", EnvironmentHelper.valueFor(someKey), is(nullValue()));
	}
}
