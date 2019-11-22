package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * This class tests the QualitySectionEncoder class
 */
class QualitySectionEncoderTest {
	@Test
	void internalEncode() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		QualitySectionEncoder encoder = new QualitySectionEncoder(new Context());
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode);

		assertThat(jsonWrapper.getString("category")).isEqualTo("quality");
		assertThat(jsonWrapper.getString("submissionMethod")).isEqualTo("electronicHealthRecord");
	}

	@Test
	void internalEncodeNegative() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		qualitySectionNode.addChildNode(new Node());

		QualitySectionEncoder encoder = new QualitySectionEncoder(new Context());
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, qualitySectionNode);

		assertThat(encoder.getErrors()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ENCODER_MISSING);
	}

	@Test
	void internalEncodeNoReportingParametersNegative() throws EncodeException {
		Node qualitySectionNode = getQualitySectionNode();
		Node removeMe = qualitySectionNode.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
		qualitySectionNode.getChildNodes().remove(removeMe);

		QualitySectionEncoder mock = mock(QualitySectionEncoder.class);
		JsonWrapper jsonWrapper = new JsonWrapper();
		mock.internalEncode(jsonWrapper, qualitySectionNode);

		verify(mock, never())
				.maintainContinuity(any(), any(Node.class), any());
	}

	/**
	 * Helper method to reduce duplication of code
	 *
	 * @return the newly constructed Quality Section Node
	 */
	private Node getQualitySectionNode() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.CEHRT, "xxxxxxxxxx12345");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		Node qualitySectionNode = new Node(TemplateId.MEASURE_SECTION_V3, clinicalDocumentNode);
		qualitySectionNode.putValue("category", "quality");
		qualitySectionNode.putValue("submissionMethod", "electronicHealthRecord");
		Node reportingParameterNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParameterNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START,"20170101");
		reportingParameterNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
		qualitySectionNode.addChildNode(reportingParameterNode);
		return qualitySectionNode;
	}
}
