package gov.cms.qpp.conversion.correlation;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.Constants.PROGRAM_NAME;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

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
				PROGRAM_NAME, "meep");
		assertThat(xpath).isNotNull();
	}

	@Test
	void verifyXpathNsSubstitution() {
		String meep = "meep";
		String path = PathCorrelator.getXpath(
				TemplateId.CLINICAL_DOCUMENT.name(), PROGRAM_NAME, meep);

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

	@Test
	void getXpathReturnsNullForUnknownKey() {
		// A template/attribute combination that will not exist in pathCorrelationMap
		String result = PathCorrelator.getXpath("UNKNOWN_TEMPLATE", "unknown_attribute", "ns");
		assertThat(result).isNull();
	}

	@Test
	void prepPathReturnsFullXpathWhenMatchFound() {
		// Build metadata with matching template + encoding label
		JsonWrapper metadata = new JsonWrapper();
		metadata.putMetadata("template", TemplateId.CLINICAL_DOCUMENT.name());
		metadata.putMetadata("nsuri", "testns");
		metadata.putMetadata(JsonWrapper.ENCODING_KEY, PROGRAM_NAME);
		metadata.putMetadata("path", "/ClinicalDocument");

		// Wrap in JsonWrapper that mimics QPP JSON
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.addMetadata(metadata);
		wrapper.put(PROGRAM_NAME, "TestValue");

		// Use a jsonPath that matches the encoding key above
		String xpath = PathCorrelator.prepPath("$." + PROGRAM_NAME, wrapper);

		assertThat(xpath).isNotEmpty();
		assertThat(xpath).contains("/ClinicalDocument");
	}
}
