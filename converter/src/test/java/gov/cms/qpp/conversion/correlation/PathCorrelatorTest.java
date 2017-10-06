package gov.cms.qpp.conversion.correlation;


import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PathCorrelatorTest {

	@Test
	public void pathCorrelatorInitilization() {
		String xpath = PathCorrelator.getXpath(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME, "meep");
		assertWithMessage("xpath should not be null").that(xpath).isNotNull();
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

	@Test
	public void verifyXpathNsSubstitution() {
		String meep = "meep";
		String path = PathCorrelator.getXpath(
				TemplateId.CLINICAL_DOCUMENT.name(), ClinicalDocumentDecoder.PROGRAM_NAME, meep);

		int meepCount = (path.length() - path.replace(meep, "").length()) / meep.length();

		assertWithMessage("3 substitutions were expected")
				.that(meepCount)
				.isEqualTo(3);
		assertWithMessage("No substitution placeholders should remain")
				.that(path.indexOf(PathCorrelator.getUriSubstitution()))
				.isEqualTo(-1);
	}
}
