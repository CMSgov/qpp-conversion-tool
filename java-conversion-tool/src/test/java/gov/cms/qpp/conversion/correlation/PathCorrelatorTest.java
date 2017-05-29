package gov.cms.qpp.conversion.correlation;


import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class PathCorrelatorTest {
	@Test
	public void pathCorrelatorInitilization() {
		String key = PathCorrelator.getKey(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME);
		String xpath = PathCorrelator.getPath(key, "meep");
		assertThat("xpath should not be null", xpath, notNullValue());
	}

	@Test(expected = PathCorrelationException.class)
	public void pathCorrelatorInitilizationNegative() throws Throwable {
		Field configPath = PathCorrelator.class.getDeclaredField("config");
		configPath.setAccessible(true);
		configPath.set(null, "meep.json");

		Method method = PathCorrelator.class.getDeclaredMethod("initPathCorrelation");
		method.setAccessible(true);
		try {
			method.invoke(null);
		} catch(InvocationTargetException ex) {
			throw ex.getCause();
		}
	}
}
