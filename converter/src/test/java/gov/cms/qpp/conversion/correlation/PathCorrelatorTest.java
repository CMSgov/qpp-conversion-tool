package gov.cms.qpp.conversion.correlation;

import com.google.common.collect.Lists;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathCorrelatorTest {

	@Test
	void testPrivateConstructor() throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, InstantiationException {
		Constructor<PathCorrelator> constructor = PathCorrelator.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	void pathCorrelatorInitilization() {
		String xpath = PathCorrelator.getXpath(TemplateId.CLINICAL_DOCUMENT.name(),
				ClinicalDocumentDecoder.PROGRAM_NAME, "meep");
		assertThat(xpath).isNotNull();
	}

	@Test
	void pathCorrelatorInitilizationNegative() throws Throwable {
		Field configPath = PathCorrelator.class.getDeclaredField("config");
		configPath.setAccessible(true);
		configPath.set(null, "meep.json");

		Method method = PathCorrelator.class.getDeclaredMethod("loadPathCorrelation");
		method.setAccessible(true);

		InvocationTargetException ex = assertThrows(InvocationTargetException.class, () -> method.invoke(null));
		assertThat(ex).hasCauseThat().isInstanceOf(PathCorrelationException.class);
	}

	@Test
	void verifyXpathNsSubstitution() {
		String meep = "meep";
		String path = PathCorrelator.getXpath(
				TemplateId.CLINICAL_DOCUMENT.name(), ClinicalDocumentDecoder.PROGRAM_NAME, meep);

		int meepCount = (path.length() - path.replace(meep, "").length()) / meep.length();

		assertThat(meepCount).isEqualTo(3);
		assertWithMessage("No substitution placeholders should remain")
				.that(path.indexOf(PathCorrelator.getUriSubstitution()))
				.isEqualTo(-1);
	}

	@Test
	void unacknowledgedEncodedLabel() {
		Map<String, String> map = new HashMap<>();
		map.put("meep", "meep");
		map.put("encodeLabel", "mawp");
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putObject("metadata_holder", Lists.newArrayList(map));

		assertThat(PathCorrelator.prepPath("$.mawp", wrapper)).isEmpty();
	}
}
