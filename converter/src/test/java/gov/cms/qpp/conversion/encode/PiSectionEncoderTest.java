package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
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
	private Node clinicalDocumentNode;

	@Captor ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

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

		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.CEHRT, "xxxxxxxxxx12345");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.MIPS_PROGRAM_NAME);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.RAW_PROGRAM_NAME, "MIPS_INDIV");

		piSectionNode = new Node(TemplateId.PI_SECTION, clinicalDocumentNode);
		piSectionNode.putValue(CATEGORY, PI);
		piSectionNode.addChildNode(piNumeratorDenominatorNode);
		piSectionNode.addChildNode(reportingParametersNode);
	}

	@Test
	void testInternalEncode() {
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiSectionEncoder piSectionEncoder = new PiSectionEncoder(new Context());
		piSectionEncoder.internalEncode(jsonWrapper, piSectionNode);

		assertWithMessage("Must have a child node").that(jsonWrapper).isNotNull();
		assertWithMessage("Must be category PI").that(jsonWrapper.getString(CATEGORY)).isEqualTo(PI);
		assertWithMessage("Must have measurements").that(jsonWrapper.get(MEASUREMENTS)).isNotNull();
		assertThat(jsonWrapper.getString(ClinicalDocumentDecoder.CEHRT)).isNotNull();
		assertWithMessage("Must have submissionMethod")
				.that(jsonWrapper.getString(SUBMISSION_METHOD)).isEqualTo(ELECTRONIC_HEALTH_RECORD);
	}

	@Test
	void aboutMetadataHolder() {
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiSectionEncoder piSectionEncoder = new PiSectionEncoder(new Context());
		piSectionEncoder.internalEncode(jsonWrapper, piSectionNode);

		Stream<JsonWrapper> failed = jsonWrapper.getMetadata().stream() // TODO asdf
			.filter(entry -> entry.getString("template").equals(TemplateId.REPORTING_PARAMETERS_ACT.name()))
			.filter(entry -> entry.getString(JsonWrapper.ENCODING_KEY).equals(""));

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

		piSectionNode.setParent(clinicalDocumentNode);

		PiSectionEncoder piSectionEncoder = new PiSectionEncoder(new Context());
		piSectionEncoder.internalEncode(testWrapper, piSectionNode);

		assertThat(piSectionEncoder.getErrors()).isNotNull();
		assertThat(piSectionEncoder.getErrors().get(0).getMessage())
				.isEqualTo(ErrorCode.CT_LABEL + "Failed to find an encoder");
	}

	@Test
	void internalEncodeNegativeWithNoReportingParameters() throws EncodeException {

		piSectionNode.getChildNodes().remove(reportingParametersNode);

		PiSectionEncoder encoder = spy(new PiSectionEncoder(new Context()));
		JsonWrapper jsonWrapper = new JsonWrapper();
		encoder.internalEncode(jsonWrapper, piSectionNode);

		//verify that maintain continuity was not called for both reporting parameters
		verify(encoder, Mockito.times(1)).maintainContinuity(any(JsonWrapper.class), any(Node.class), anyString());
	}
}
