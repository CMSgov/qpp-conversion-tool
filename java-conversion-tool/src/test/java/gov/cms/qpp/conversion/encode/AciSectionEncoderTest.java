package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AciSectionEncoderTest {

	private static final String CATEGORY = "category";
	private static final String ACI = "aci";
	private static final String MEASUREMENTS = "measurements";
	private static final String SUBMISSION_METHOD = "submissionMethod";
	private static final String ELECTRONIC_HEALTH_RECORD = "electronicHealthRecord";
	private static final String MEASUREMENT_ID = "measureId";
	private static final String MEASUREMENT_ID_VALUE = "ACI-PEA-1";
	private static final String AGGREGATE_COUNT_ID = "aggregateCount";

	private Node aciSectionNode;
	private Node aciNumeratorDenominatorNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;

	@Before
	public void createNode() {
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

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue(CATEGORY, ACI);
		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);
	}

	@Test
	public void testInternalEncode() {
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciSectionEncoder aciSectionEncoder = new AciSectionEncoder();
		aciSectionEncoder.internalEncode(jsonWrapper, aciSectionNode);
		Map<?, ?> testMapObject = (Map<?, ?>) jsonWrapper.getObject();

		assertThat("Must have a child node", testMapObject, is(not(nullValue())));
		assertThat("Must be category ACI", testMapObject.get(CATEGORY), is(ACI));
		assertThat("Must have measurements", testMapObject.get(MEASUREMENTS), is(not(nullValue())));
		assertThat("Must have submissionMethod", testMapObject.get(SUBMISSION_METHOD), is(ELECTRONIC_HEALTH_RECORD));
	}

	@Test
	public void testInternalEncodeWithNoChildren() {
		JsonWrapper testWrapper = new JsonWrapper();

		Node invalidAciNumeratorDenominatorNode = new Node();

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue(CATEGORY, ACI);
		aciSectionNode.addChildNode(invalidAciNumeratorDenominatorNode);

		AciSectionEncoder aciSectionEncoder = new AciSectionEncoder();
		aciSectionEncoder.internalEncode(testWrapper, aciSectionNode);

		assertThat("Must have validation error.", aciSectionEncoder.getDetails(), is(not(nullValue())));
		assertThat("Must be correct validation error", aciSectionEncoder.getDetails().get(0).getMessage(),
				is("Failed to find an AciSectionEncoder"));
	}
}
