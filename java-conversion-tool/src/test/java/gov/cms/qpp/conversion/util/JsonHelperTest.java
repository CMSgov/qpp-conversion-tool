package gov.cms.qpp.conversion.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.Test;
import org.reflections.util.ClasspathHelper;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
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
		configurations = JsonHelper.readJsonAtJsonPath(measuresInput, "$",List.class);
		assertThat("Expect to get a List of measureConfigs", configurations,is(not(empty())));
	}
}