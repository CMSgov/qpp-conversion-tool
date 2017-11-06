package gov.cms.qpp.test;

import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class PropertiesTestSuite {

	private Properties properties;

	@BeforeEach
	public final void saveProperties() {
		properties = new Properties();
		properties.putAll(System.getProperties());
	}

	@AfterEach
	public final void resetProperties() {
		System.setProperties(properties);
	}

}
