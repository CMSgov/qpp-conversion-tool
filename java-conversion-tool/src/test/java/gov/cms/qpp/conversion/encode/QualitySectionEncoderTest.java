package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
/**
 * This class tests the QualitySectionEncoder class
 */
public class QualitySectionEncoderTest {
	@Test
	public void internalEncode() throws Exception {
		Node qualitySectionNode = new Node(TemplateId.MEASURE_SECTION_V2.getTemplateId());
		qualitySectionNode.putValue("category", "quality");
		qualitySectionNode.putValue("submissionMethod", "cmsWebInterface");
		QualitySectionEncoder encoder = new QualitySectionEncoder();
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode );

		assertThat("Expect to encode category", jsonWrapper.getString("category"), is("quality"));
		assertThat("Expect to encode submissionMethod", jsonWrapper.getString("submissionMethod"), is("cmsWebInterface"));
	}

}