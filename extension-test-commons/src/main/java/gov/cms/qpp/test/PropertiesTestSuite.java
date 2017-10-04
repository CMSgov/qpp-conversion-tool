package gov.cms.qpp.test;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;

public class PropertiesTestSuite {

	private Properties properties;

	@Before
	public final void saveProperties() {
		properties = new Properties();
		properties.putAll(System.getProperties());
	}

	@After
	public final void resetProperties() {
		System.setProperties(properties);
	}

}
