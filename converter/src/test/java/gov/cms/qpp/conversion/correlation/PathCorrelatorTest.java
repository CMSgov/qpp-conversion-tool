package gov.cms.qpp.conversion.correlation;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.TemplateId;

class PathCorrelatorTest {

	@Test
	void testPrivateConstructor() throws Exception {
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
		JsonWrapper metadata = new JsonWrapper();
		metadata.putMetadata("meep", "meep");
		metadata.putMetadata(JsonWrapper.ENCODING_KEY, "mawp");

		JsonWrapper wrapper = new JsonWrapper();
		wrapper.addMetadata(metadata);
		wrapper.put("mop","mop"); // TODO asdf test list object. note that JSON require top level to be object not list
		
		String actual = PathCorrelator.prepPath("$.mawp", wrapper);
		assertThat(actual).isEmpty();
	}

	@Test
	void unacknowledgedEncodedLabel_multipleMetadata() {
		JsonWrapper metadata = new JsonWrapper();
		metadata.putMetadata("meep", "meep");
		metadata.putMetadata(JsonWrapper.ENCODING_KEY, "mawp");
		JsonWrapper metadata2 = new JsonWrapper();
		metadata2.putMetadata("template", "mip");
		metadata2.putMetadata("nsuri", "mip");
		metadata2.putMetadata(JsonWrapper.ENCODING_KEY, "mip");
		// TODO asdf what about encodedLabel empty string?
		// TODO asdf what about prepPath returning something other than empty?

		JsonWrapper wrapper = new JsonWrapper();
		wrapper.addMetadata(metadata);
		wrapper.addMetadata(metadata2);
		wrapper.put("mop","mop");
		
		String actual = PathCorrelator.prepPath("$.mawp", wrapper);
		assertThat(actual).isEmpty();
	}
}
