package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class AciSectionEncoderTest {

	private static final String CATEGORY = "category";
	private static final String ACI = "aci";
	private static final String MEASUREMENTS = "measurements";
	private static final String SUBMISSION_METHOD = "submissionMethod";
	private static final String ELECTRONIC_HEALTH_RECORD = "electronicHealthRecord";
	private static final String MEASUREMENT_ID = "measureId";
	private static final String MEASUREMENT_ID_VALUE = "ACI-PEA-1";
	private static final String AGGREGATE_COUNT_ID = "aggregateCount";

	private Node aciSectionNode;
	private Node reportingParametersNode;
	private Node aciNumeratorDenominatorNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;

	@BeforeEach
	void createNode() {
		numeratorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode.putValue(AGGREGATE_COUNT_ID, "400");

		denominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue(AGGREGATE_COUNT_ID, "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciNumeratorDenominatorNode.addChildNode(aciProportionNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciProportionDenominatorNode);
		aciNumeratorDenominatorNode.putValue(MEASUREMENT_ID, MEASUREMENT_ID_VALUE);

		reportingParametersNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START,"20170101");
		reportingParametersNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END,"20171231");

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue(CATEGORY, ACI);
		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);
		aciSectionNode.addChildNode(reportingParametersNode);
	}

	@Test
	void testInternalEncode() {
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciSectionEncoder aciSectionEncoder = new AciSectionEncoder(new Context());
		aciSectionEncoder.internalEncode(jsonWrapper, aciSectionNode);

		Map<?, ?> testMapObject = (Map<?, ?>) jsonWrapper.getObject();

		assertWithMessage("Must have a child node").that(testMapObject).isNotNull();
		assertWithMessage("Must be category ACI").that(testMapObject.get(CATEGORY)).isEqualTo(ACI);
		assertWithMessage("Must have measurements").that(testMapObject.get(MEASUREMENTS)).isNotNull();
		assertWithMessage("Must have submissionMethod")
				.that(testMapObject.get(SUBMISSION_METHOD)).isEqualTo(ELECTRONIC_HEALTH_RECORD);
	}

	@Test
	void testInternalEncodeWithNoChildren() {
		JsonWrapper testWrapper = new JsonWrapper();

		Node invalidAciNumeratorDenominatorNode = new Node();

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue(CATEGORY, ACI);
		aciSectionNode.addChildNode(invalidAciNumeratorDenominatorNode);
		aciSectionNode.addChildNode(reportingParametersNode);

		AciSectionEncoder aciSectionEncoder = new AciSectionEncoder(new Context());
		aciSectionEncoder.internalEncode(testWrapper, aciSectionNode);

		assertWithMessage("Must have validation error.")
				.that(aciSectionEncoder.getDetails()).isNotNull();
		assertWithMessage("Must be correct validation error")
				.that(aciSectionEncoder.getDetails().get(0).getMessage())
				.isEqualTo("Failed to find an encoder");
	}

	@Test
	void internalEncodeNegativeWithNoReportingParameters() throws EncodeException {

		aciSectionNode.getChildNodes().remove(reportingParametersNode);

		AciSectionEncoder encoder = spy(new AciSectionEncoder(new Context()));
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, aciSectionNode);

		verify(encoder, never()).maintainContinuity(any(JsonWrapper.class), any(Node.class), anyString());
	}
}
