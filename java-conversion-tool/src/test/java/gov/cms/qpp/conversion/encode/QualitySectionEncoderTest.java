package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

	@Test(expected = EncodeException.class)
	public void internalEncodeNegative() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		qualitySectionNode.addChildNode(new Node());

		QualitySectionEncoder encoder = new QualitySectionEncoder();
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode);
	}

	@Test
	public void internalEncodeNoReportingParametersNegative() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		Node removeMe = qualitySectionNode.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
		qualitySectionNode.getChildNodes().remove(removeMe);

		QualitySectionEncoder mock = mock(QualitySectionEncoder.class);
		JsonWrapper jsonWrapper = new JsonWrapper();
		mock.internalEncode(jsonWrapper, qualitySectionNode);

		verify(mock, never())
				.maintainContinuity(anyObject(), any(JsonWrapper.class), anyObject());
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
		Node reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParameterNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START,"20170101");
		reportingParameterNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
		qualitySectionNode.addChildNode(reportingParameterNode);
		return qualitySectionNode;
	}
}
