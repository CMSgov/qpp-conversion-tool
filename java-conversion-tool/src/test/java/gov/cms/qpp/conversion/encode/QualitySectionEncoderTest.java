package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.encode.helper.RegistryHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.xml.XmlException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * This class tests the QualitySectionEncoder class
 */
public class QualitySectionEncoderTest {
	@Test
	public void internalEncode() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		QualitySectionEncoder encoder = new QualitySectionEncoder();
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode);

		assertThat("Expect to encode category", jsonWrapper.getString("category"), is("quality"));
		assertThat("Expect to encode submissionMethod", jsonWrapper.getString("submissionMethod"), is("cmsWebInterface"));
	}

	/**
	 * Tests for the missing child encoder
	 *
	 * @throws XmlException           when parsing a xml fragment fails
	 * @throws NoSuchFieldException   Java Reflection Api error if field is not in object
	 * @throws IllegalAccessException Thrown if a Security Manager is present
	 */
	@Test
	public void missingEncoderTest() throws XmlException, NoSuchFieldException, IllegalAccessException {

		Registry<JsonOutputEncoder> validRegistry = QppOutputEncoder.ENCODERS;

		Registry<JsonOutputEncoder> invalidRegistry = RegistryHelper.makeInvalidRegistry( //This will be the classname of the child ENCODERS
				"gov.cms.qpp.conversion.encode.MeasureDataEncoder");

		boolean exception = false;
		RegistryHelper.setEncoderRegistry(invalidRegistry); //Set Registry with missing class

		Node qualitySectionNode = getQualitySectionNode();
		Node measureDataNode = new Node(TemplateId.MEASURE_DATA_CMS_V2, qualitySectionNode);
		measureDataNode.putValue("SomeValueKey", "SomeValueData");
		qualitySectionNode.addChildNode(measureDataNode);
		QualitySectionEncoder encoder = new QualitySectionEncoder();
		JsonWrapper jsonWrapper = new JsonWrapper();

		try {
			encoder.internalEncode(jsonWrapper, qualitySectionNode);
		} catch (EncodeException | NullPointerException e) {
			exception = true;
		}
		assertThat("Expecting Encode Exception", exception, is(true));
		RegistryHelper.setEncoderRegistry(validRegistry); //Restore Registry
	}

	/**
	 * Helper method to reduce duplication of code
	 *
	 * @return the newly constructed Quality Section Node
	 */
	private Node getQualitySectionNode() {
		Node qualitySectionNode = new Node(TemplateId.MEASURE_SECTION_V2);
		qualitySectionNode.putValue("category", "quality");
		qualitySectionNode.putValue("submissionMethod", "cmsWebInterface");
		return qualitySectionNode;
	}
}
