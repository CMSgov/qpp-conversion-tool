package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;

public class AciSectionEncoderTest {

	private static final String ACI_SECTION_ID = "2.16.840.1.113883.10.20.27.2.5";
	private static final String ACI_PROPORTION_MEASURE_ID = "2.16.840.1.113883.10.20.27.3.28";
	private static final String ACI_PROPORTION_NUMERATOR_NODE_ID = "2.16.840.1.113883.10.20.27.3.31";
	private static final String ACI_PROPORTION_DENOMINATOR_NODE_ID = "2.16.840.1.113883.10.20.27.3.32";
	private static final String NUMERATOR_NODE_ID = "2.16.840.1.113883.10.20.27.3.3";
	private static final String DENOMINATOR_NODE_ID = "2.16.840.1.113883.10.20.27.3.3";
	private static final String CATEGORY = "category";
	private static final String ACI = "aci";
	private static final String MEASUREMENTS = "measurements";
	private static final String MEASUREMENT_ID = "measureId";
	private static final String MEASUREMENT_ID_VALUE = "ACI-PEA-1";
	private static final String AGGREGATE_COUNT_ID = "aggregateCount";

	private static final String JSON_FORMAT_EXPECT = "{\n  \""+ CATEGORY +"\" : \"" + ACI + "\",\n  \"" + MEASUREMENTS + "\" : [ "
			+ "{\n    \"" + MEASUREMENT_ID + "\" : \"" + MEASUREMENT_ID_VALUE + "\",\n    \"value\" : {\n"
			+ "      \"numerator\" : 400,\n      \"denominator\" : 600\n    }\n  } ]\n" + "}";

	private Node aciSectionNode;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;

	@Before
	public void createNode() {
		numeratorValueNode = new Node(NUMERATOR_NODE_ID);
		numeratorValueNode.putValue(AGGREGATE_COUNT_ID, "400");

		denominatorValueNode = new Node(DENOMINATOR_NODE_ID);
		denominatorValueNode.putValue(AGGREGATE_COUNT_ID, "600");

		aciProportionDenominatorNode = new Node(ACI_PROPORTION_DENOMINATOR_NODE_ID);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionNumeratorNode = new Node(ACI_PROPORTION_NUMERATOR_NODE_ID);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionMeasureNode = new Node(ACI_PROPORTION_MEASURE_ID);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue(MEASUREMENT_ID, MEASUREMENT_ID_VALUE);

		aciSectionNode = new Node(ACI_SECTION_ID);
		aciSectionNode.putValue(CATEGORY, ACI);
		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Validations.init();
	}

	@After
	public void tearDown() {
		Validations.clear();
	}


	@Test
	public void testInternalEncode() throws EncodeException {
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciSectionEncoder aciSectionEncoder = new AciSectionEncoder();
		aciSectionEncoder.internalEncode(jsonWrapper, aciSectionNode);
		Map<?, ?> testMapObject = (Map<?, ?>) jsonWrapper.getObject();

		assertThat("Must have a child node", testMapObject, is(not(nullValue())));
		assertThat("Must be category ACI", testMapObject.get(CATEGORY), is(ACI));
		assertThat("Must have measurements", testMapObject.get(MEASUREMENTS), is(not(nullValue())));
		assertThat("Must return correct json format", jsonWrapper.toString(), is(JSON_FORMAT_EXPECT));
	}

	@Test
	public void testInternalEncodeWithNoChildren() throws EncodeException {
		JsonWrapper testWrapper = new JsonWrapper();

		final String invalidMeasureNode = "invalidMeasureNode";
		Node invalidAciProportionMeasureNode = new Node(invalidMeasureNode);

		aciSectionNode = new Node(ACI_SECTION_ID);
		aciSectionNode.putValue(CATEGORY, ACI);
		aciSectionNode.addChildNode(invalidAciProportionMeasureNode);

		AciSectionEncoder aciSectionEncoder = new AciSectionEncoder();
		aciSectionEncoder.internalEncode(testWrapper, aciSectionNode);

		assertThat("Must have validation error.", aciSectionEncoder.getValidationsById(invalidMeasureNode), is(not(nullValue())));
		assertThat("Must be correct validation error", aciSectionEncoder.getValidationsById(invalidMeasureNode).get(0),
				is("Failed to find an AciSectionEncoder"));
	}
}
