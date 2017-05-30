package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentEncoderTest {

	private Node aciSectionNode;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private Node aciProportionMeasureNode2;
	private Node aciProportionNumeratorNode2;
	private Node aciProportionDenominatorNode2;
	private Node numeratorValueNode2;
	private Node denominatorValueNode2;
	private Node aciProportionMeasureNode3;
	private Node aciProportionNumeratorNode3;
	private Node aciProportionDenominatorNode3;
	private Node numeratorValueNode3;
	private Node denominatorValueNode3;
	private Node reportingParametersActNode;
	private Node reportingParametersSectionNode;
	private Node clinicalDocumentNode;
	private List<Node> nodes;

	@Before
	public void createNode() {

		numeratorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode.putValue("aggregateCount", "400");

		numeratorValueNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode2.putValue("aggregateCount", "500");

		numeratorValueNode3 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode3.putValue("aggregateCount", "400");

		denominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue("aggregateCount", "600");

		denominatorValueNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode2.putValue("aggregateCount", "700");

		denominatorValueNode3 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode3.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue("measureId", "ACI_EP_1");

		aciProportionMeasureNode3 = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue("measureId", "ACI_CCTPE_3");

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersActNode.putValue("performanceStart", "20170101");
		reportingParametersActNode.putValue("performanceEnd", "20171231");

		reportingParametersSectionNode = new Node(TemplateId.REPORTING_PARAMETERS_SECTION);
		reportingParametersSectionNode.addChildNode(reportingParametersActNode);


		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("entityType", "individual");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.addChildNode(reportingParametersSectionNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);

		nodes = new ArrayList<>();
		nodes.add(clinicalDocumentNode);
	}

	@Test
	public void testInternalEncode() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());

		assertThat("Must have a correct program name", clinicalDocMap.get("programName"), is("mips"));

		assertThat("Must have a correct entityType", clinicalDocMap.get("entityType"), is("individual"));

		assertThat("Must have a correct taxpayerIdentificationNumber",
				clinicalDocMap.get("taxpayerIdentificationNumber"), is("123456789"));

		assertThat("Must have a correct nationalProviderIdentifier",
				clinicalDocMap.get("nationalProviderIdentifier"), is("2567891421"));

		assertThat("Must have a correct performanceYear", clinicalDocMap.get("performanceYear"), is(2017));
	}

	@Test(expected = EncodeException.class)
	public void testInternalEncodeNegative() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentNode.addChildNode(new Node("meep"));
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);
	}

	@Test
	public void testInternalEncoderWithoutReporting() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(reportingParametersSectionNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());

		assertThat("Must not be a performanceStart because the reporting parameters was missing.",
			((List<Map<?, ?>>)clinicalDocMap.get("measurementSets")).get(0).get("performanceStart"), is(nullValue()));
		assertThat("Must not be a performanceEnd because the reporting parameters was missing.",
			((List<Map<?, ?>>)clinicalDocMap.get("measurementSets")).get(0).get("performanceEnd"), is(nullValue()));
	}

	@Test
	public void testInternalEncodeWithoutMeasures() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

<<<<<<< HEAD
		assertThat("Must return a Clinical Document without measurement section", testJsonWrapper.toString(),
				is(EXPECTED_NO_ACI));
	}

	@Test
	public void testInvalidEncoder()throws Exception {
		boolean exception = false;
		Registry<JsonOutputEncoder> invalidRegistry =
				RegistryHelper.makeInvalidRegistry("gov.cms.qpp.conversion.encode.AciSectionEncoder");
		Registry<JsonOutputEncoder> validRegistry = QppOutputEncoder.ENCODERS;

		JsonWrapper testJsonWrapper = new JsonWrapper();
		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();

		RegistryHelper.setEncoderRegistry(invalidRegistry); //Set Registry with missing class
		try {
			clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);
		} catch (EncodeException e) {
			exception = true;
		}
		assertThat("Expecting Encode Exception", exception, is(true));
=======
		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());
>>>>>>> 882897d9420680d6b881505a8292857495843042

		assertThat("Must not be a performanceStart because the reporting parameters was missing.",
			clinicalDocMap.get("measurementSets"), is(nullValue()));
	}
}
