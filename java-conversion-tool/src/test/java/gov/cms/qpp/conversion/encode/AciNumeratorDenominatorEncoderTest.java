package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AciNumeratorDenominatorEncoderTest {

	private static final String MEASURE_ID = "ACI-PEA-1";
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private List<Node> nodes;

	@Before
	public void createNode() {
		numeratorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode.putValue("aggregateCount", "400");

		denominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setType(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", MEASURE_ID);

		nodes = new ArrayList<>();
		nodes.add(aciProportionMeasureNode);
	}

	@Test
	public void testEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder();

		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		String EXPECTED = "{\n  \"measureId\" : \"" + MEASURE_ID + "\",\n  \"value\" : {\n    \"numerator\" : 400,\n    \"denominator\" : 600\n  }\n}";
		Assert.assertEquals(EXPECTED, sw.toString());
		assertThat("expected encoder to return a json representation of a measure node", sw.toString(), is(EXPECTED));
	}

	@Test
	public void testInternalEncode() throws EncodeException {

		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciNumeratorDenominatorEncoder objectUnderTest = new AciNumeratorDenominatorEncoder();

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciProportionMeasureNode);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertThat("The internal object of the jsonWrapper must not be null", jsonWrapper.getObject(), is(not(nullValue())));
		assertThat("The internal object of the jsonWrapper must be a Map", jsonWrapper.getObject(), is(instanceOf(Map.class)));
		assertThat("The internal object must have an attribute named value", ((Map<?, ?>)jsonWrapper.getObject()).get("value"), is(not(nullValue())));
	}

	@Test
	public void testNoChildEncoder() throws EncodeException {
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciNumeratorDenominatorEncoder objectUnderTest = new AciNumeratorDenominatorEncoder();
		Node unknownNode = new Node();
		aciProportionMeasureNode.addChildNode(unknownNode);

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciProportionMeasureNode);

		//assert
		assertThat("There must be a single validation error", objectUnderTest.getValidationErrors(), hasSize(1));
		assertThat("The validation error must be the inability to find an encoder", objectUnderTest.getValidationErrors().get(0).getErrorText(), is("Failed to find an encoder"));
	}
}
