package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentEncoderTest {

	private static final String EXPECTED_CLINICAL_DOC_FORMAT = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\"," + "\n  \"performanceYear\" : 2017,"
			+ "\n  \"measurementSets\" : [ " + "{\n    \"category\" : \"aci\",\n    \"measurements\" : [ "
			+ "{\n      \"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_EP_1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 500,\n        \"denominator\" : 700\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_CCTPE_3\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],"
			+ "\n    \"source\" : \"provider\"," + "\n    \"performanceStart\" : \"2017-01-01\","
			+ "\n    \"performanceEnd\" : \"2017-12-31\"" + "\n  } ]\n}";

	private static final String EXPECTED_NO_REPORTING = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\","
			+ "\n  \"measurementSets\" : [ " + "{\n    \"category\" : \"aci\",\n    \"measurements\" : [ "
			+ "{\n      \"measureId\" : \"ACI-PEA-1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_EP_1\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 500,\n        \"denominator\" : 700\n      }\n    }, "
			+ "{\n      \"measureId\" : \"ACI_CCTPE_3\",\n      \"value\" : {\n"
			+ "        \"numerator\" : 400,\n        \"denominator\" : 600\n      }\n    } ],"
			+ "\n    \"source\" : \"provider\"\n  } ]\n}";

	private static final String EXPECTED_NO_ACI = "{\n  \"programName\" : \"mips\"," + "\n  \"entityType\" : \"individual\","
			+ "\n  \"taxpayerIdentificationNumber\" : \"123456789\","
			+ "\n  \"nationalProviderIdentifier\" : \"2567891421\"," + "\n  \"performanceYear\" : 2017\n}";

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

		numeratorValueNode = new Node();
		numeratorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode.putValue("aggregateCount", "400");

		numeratorValueNode2 = new Node();
		numeratorValueNode2.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode2.putValue("aggregateCount", "500");

		numeratorValueNode3 = new Node();
		numeratorValueNode3.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode3.putValue("aggregateCount", "400");

		denominatorValueNode = new Node();
		denominatorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode.putValue("aggregateCount", "600");

		denominatorValueNode2 = new Node();
		denominatorValueNode2.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode2.putValue("aggregateCount", "700");

		denominatorValueNode3 = new Node();
		denominatorValueNode3.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode3.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node();
		aciProportionDenominatorNode2.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node();
		aciProportionDenominatorNode3.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node();
		aciProportionNumeratorNode2.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node();
		aciProportionNumeratorNode3.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node();
		aciProportionMeasureNode2.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue("measureId", "ACI_EP_1");

		aciProportionMeasureNode3 = new Node();
		aciProportionMeasureNode3.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue("measureId", "ACI_CCTPE_3");

		aciSectionNode = new Node();
		aciSectionNode.setId(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		reportingParametersActNode = new Node();
		reportingParametersActNode.setId(TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		reportingParametersActNode.putValue("performanceStart", "20170101");
		reportingParametersActNode.putValue("performanceEnd", "20171231");

		reportingParametersSectionNode = new Node();
		reportingParametersSectionNode.setId(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		reportingParametersSectionNode.addChildNode(reportingParametersActNode);


		clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
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

		assertThat("Must have correct json formatting", testJsonWrapper.toString(),
				is(EXPECTED_CLINICAL_DOC_FORMAT));
	}

	@Test
	public void testInternalEncoderWithoutReporting() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(reportingParametersSectionNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		assertThat("Must return a Clinical Document without reporting parameter section", testJsonWrapper.toString(),
				is(EXPECTED_NO_REPORTING));
	}

	@Test
	public void testInternalEncodeWithoutMeasures() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder();
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		assertThat("Must return a Clinical Document without measurement section", testJsonWrapper.toString(),
				is(EXPECTED_NO_ACI));
	}
}
