package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import org.junit.After;
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

public class AciProportionMeasureEncoderTest {

	private Node aciPerformanceRate;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private List<Node> nodes;

	private static final String MEASURE_ID = "ACI-PEA-1";

	@Before
	public void createNode() {
		numeratorValueNode = new Node();
		numeratorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		numeratorValueNode.putValue("aggregateCount", "400");

		denominatorValueNode = new Node();
		denominatorValueNode.setId("2.16.840.1.113883.10.20.27.3.3");
		denominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId("2.16.840.1.113883.10.20.27.3.32");
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId("2.16.840.1.113883.10.20.27.3.31");
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);
		
		aciPerformanceRate = new Node();
		aciPerformanceRate.setId("2.16.840.1.113883.10.20.27.3.30");
		aciPerformanceRate.putValue("DefaultDecoderFor", "Performance Rate");

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.addChildNode(aciPerformanceRate);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", MEASURE_ID);

		nodes = new ArrayList<>();
		nodes.add(aciProportionMeasureNode);

		Validations.init();
	}

	@After
	public void tearDown() {
		Validations.clear();
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
		AciProportionMeasureEncoder objectUnderTest = new AciProportionMeasureEncoder();

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

		//set-up
		final String unknownNodeId = "unknownNodeId";

		JsonWrapper jsonWrapper = new JsonWrapper();
		AciProportionMeasureEncoder objectUnderTest = new AciProportionMeasureEncoder();
		Node unknownNode = new Node();
		unknownNode.setId(unknownNodeId);
		aciProportionMeasureNode.addChildNode(unknownNode);

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciProportionMeasureNode);

		//assert
		assertThat("There must be a single validation error", objectUnderTest.getValidationsById(unknownNodeId), hasSize(1));
		assertThat("The validation error must be the inability to find an encoder", objectUnderTest.getValidationsById(unknownNodeId).get(0), is("Failed to find an encoder"));
	}
}
