package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;

class PiSectionEncoderTest {

	private static final String CATEGORY = "category";
	private static final String PI = "pi";
	private static final String MEASUREMENTS = "measurements";
	private static final String SUBMISSION_METHOD = "submissionMethod";
	private static final String ELECTRONIC_HEALTH_RECORD = "electronicHealthRecord";
	private static final String MEASUREMENT_ID = "measureId";
	private static final String MEASUREMENT_ID_VALUE = "ACI-PEA-1";
	private static final String AGGREGATE_COUNT_ID = "aggregateCount";

	private Node piSectionNode;
	private Node reportingParametersNode;
	private Node piNumeratorDenominatorNode;
	private Node piProportionNumeratorNode;
	private Node piProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;

	@BeforeEach
	void createNode() {
		numeratorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		numeratorValueNode.putValue(AGGREGATE_COUNT_ID, "400");

		denominatorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		denominatorValueNode.putValue(AGGREGATE_COUNT_ID, "600");

		piProportionDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		piProportionDenominatorNode.addChildNode(denominatorValueNode);

		piProportionNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		piProportionNumeratorNode.addChildNode(numeratorValueNode);

		piNumeratorDenominatorNode = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		piNumeratorDenominatorNode.addChildNode(piProportionNumeratorNode);
		piNumeratorDenominatorNode.addChildNode(piProportionDenominatorNode);
		piNumeratorDenominatorNode.putValue(MEASUREMENT_ID, MEASUREMENT_ID_VALUE);

		reportingParametersNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START,"20170101");
		reportingParametersNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END,"20171231");

		piSectionNode = new Node(TemplateId.PI_SECTION);
		piSectionNode.putValue(CATEGORY, PI);
		piSectionNode.addChildNode(piNumeratorDenominatorNode);
		piSectionNode.addChildNode(reportingParametersNode);
	}

	@Test
	void testInternalEncode() {
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiSectionEncoder piSectionEncoder = new PiSectionEncoder(new Context());
		piSectionEncoder.internalEncode(jsonWrapper, piSectionNode);

		Map<?, ?> testMapObject = (Map<?, ?>) jsonWrapper.getObject();

		assertWithMessage("Must have a child node").that(testMapObject).isNotNull();
		assertWithMessage("Must be category ACI").that(testMapObject.get(CATEGORY)).isEqualTo(PI);
		assertWithMessage("Must have measurements").that(testMapObject.get(MEASUREMENTS)).isNotNull();
		assertWithMessage("Must have submissionMethod")
				.that(testMapObject.get(SUBMISSION_METHOD)).isEqualTo(ELECTRONIC_HEALTH_RECORD);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void aboutMetadataHolder() {
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiSectionEncoder piSectionEncoder = new PiSectionEncoder(new Context());
		piSectionEncoder.internalEncode(jsonWrapper, piSectionNode);

		Map<?, ?> testMapObject = (Map<?, ?>) jsonWrapper.getObject();
		Stream failed = ((Set) testMapObject.get("metadata_holder")).stream()
			.filter(entry -> ((Map) entry).get("template").equals(TemplateId.REPORTING_PARAMETERS_ACT.name()))
			.filter(entry -> ((Map) entry).get("encodeLabel").equals(""));

		assertThat(failed.count()).isEqualTo(0);
	}

	@Test
	void testInternalEncodeWithNoChildren() {
		JsonWrapper testWrapper = new JsonWrapper();

		Node invalidAciNumeratorDenominatorNode = new Node();

		piSectionNode = new Node(TemplateId.PI_SECTION);
		piSectionNode.putValue(CATEGORY, PI);
		piSectionNode.addChildNode(invalidAciNumeratorDenominatorNode);
		piSectionNode.addChildNode(reportingParametersNode);

		PiSectionEncoder piSectionEncoder = new PiSectionEncoder(new Context());
		piSectionEncoder.internalEncode(testWrapper, piSectionNode);

		assertThat(piSectionEncoder.getDetails()).isNotNull();
		assertThat(piSectionEncoder.getDetails().get(0).getMessage())
				.isEqualTo(ErrorCode.CT_LABEL + "Failed to find an encoder");
	}

	@Test
	void internalEncodeNegativeWithNoReportingParameters() throws EncodeException {

		piSectionNode.getChildNodes().remove(reportingParametersNode);

		PiSectionEncoder encoder = spy(new PiSectionEncoder(new Context()));
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, piSectionNode);

		verify(encoder, never()).maintainContinuity(any(JsonWrapper.class), any(Node.class), anyString());
	}
}
