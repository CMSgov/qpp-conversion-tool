package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.ConverterTestHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class AciMeasurePerformedRnREncoderTest {

	private static final String MEASURE_ID = "ACI_INFBLO_1";
	private static final String VALUE = "Y";

	private List<Node> nodes;
	private Node aciMeasurePerformedRnR;
	private Node measurePerformed;

	@Before
	public void createNode() {
		aciMeasurePerformedRnR = new Node(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		aciMeasurePerformedRnR.putValue("measureId", MEASURE_ID);

		measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformed.putValue("measurePerformed", VALUE);

		aciMeasurePerformedRnR.addChildNode(measurePerformed);

		nodes = new ArrayList<>();
		nodes.add(aciMeasurePerformedRnR);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEncoder() throws IOException {
		QppOutputEncoder encoder = new QppOutputEncoder(ConverterTestHelper.newMockConverter());
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		Map<String, Object> content = JsonHelper.readJson(new ByteArrayInputStream(sw.toString().getBytes()), Map.class);
		assertThat("measureId should be " + MEASURE_ID, content.get("measureId"), is(MEASURE_ID));
		assertThat("value should be true " , content.get("value"), is (true));
	}

	@Test
	public void testInternalEncode() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(ConverterTestHelper.newMockConverter());

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertThat("The value must be " + VALUE, jsonWrapper.getBoolean("value"), is(true));
	}

	@Test
	public void testInternalEncodeNoChildNoValue(){
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(ConverterTestHelper.newMockConverter());
		aciMeasurePerformedRnR.setChildNodes();

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);
		System.out.println(aciMeasurePerformedRnR.findFirstNode(TemplateId.MEASURE_PERFORMED));

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertNull("There should be no " + VALUE, jsonWrapper.getString("value"));
	}

	@Test
	public void testInternalEncodeBooleanTrueValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(ConverterTestHelper.newMockConverter());
		aciMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed", "Y");
		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertThat("There value should be true " + VALUE, jsonWrapper.getBoolean("value"), is(true));

	}

	@Test
	public void testInternalEncodeBooleanFalseValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(ConverterTestHelper.newMockConverter());
		aciMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed","N");
		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertThat("There value should be false " + VALUE, jsonWrapper.getBoolean("value"), is(false));

	}

	@Test
	public void testInternalEncodeBooleanStringValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(ConverterTestHelper.newMockConverter());
		String unknownValue = "Some unknown value";
		aciMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed",unknownValue);
		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertThat("There value should be some string value ", jsonWrapper.getString("value"), is(unknownValue));
	}
}
