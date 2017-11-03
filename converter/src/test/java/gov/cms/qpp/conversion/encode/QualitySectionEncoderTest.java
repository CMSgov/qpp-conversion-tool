package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
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
		QualitySectionEncoder encoder = new QualitySectionEncoder(new Context());
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode);

		assertWithMessage("Expect to encode category")
				.that(jsonWrapper.getString("category")).isEqualTo("quality");
		assertWithMessage("Expect to encode submissionMethod")
				.that(jsonWrapper.getString("submissionMethod")).isEqualTo("electronicHealthRecord");
	}

	@Test
	public void internalEncodeNegative() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		qualitySectionNode.addChildNode(new Node());

		QualitySectionEncoder encoder = new QualitySectionEncoder(new Context());
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode);

		assertWithMessage("An encoder for a child node should not have been found.")
				.that(encoder.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ENCODER_MISSING);
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
				.maintainContinuity(any(), any(JsonWrapper.class), any());
	}

	/**
	 * Helper method to reduce duplication of code
	 *
	 * @return the newly constructed Quality Section Node
	 */
	private Node getQualitySectionNode() {
		Node qualitySectionNode = new Node(TemplateId.MEASURE_SECTION_V2);
		qualitySectionNode.putValue("category", "quality");
		qualitySectionNode.putValue("submissionMethod", "electronicHealthRecord");
		Node reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParameterNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START,"20170101");
		reportingParameterNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
		qualitySectionNode.addChildNode(reportingParameterNode);
		return qualitySectionNode;
	}
}
