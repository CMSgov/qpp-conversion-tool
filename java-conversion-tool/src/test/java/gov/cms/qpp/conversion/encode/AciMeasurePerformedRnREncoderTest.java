package gov.cms.qpp.conversion.encode;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class AciMeasurePerformedRnREncoderTest {

	private static final String MEASURE_ID = "ACI_INFBLO_1";
	private static final String VALUE = "true";

	private List<Node> nodes;
	private Node aciMeasurePerformedRnR;
	private Node measurePerformed;

	@Before
	public void createNode() {
		aciMeasurePerformedRnR = new Node();
		aciMeasurePerformedRnR.setId(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS.getTemplateId());
		aciMeasurePerformedRnR.putValue("measureId", MEASURE_ID);

		measurePerformed = new Node();
		measurePerformed.setId(TemplateId.MEASURE_PERFORMED.getTemplateId());
		measurePerformed.putValue("measurePerformed", VALUE);

		aciMeasurePerformedRnR.addChildNode(measurePerformed);

		nodes = new ArrayList<>();
		nodes.add(aciMeasurePerformedRnR);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEncoder() throws IOException {
		QppOutputEncoder encoder = new QppOutputEncoder();
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}

		Map<String, String> content = JsonHelper.readJson(new ByteArrayInputStream(sw.toString().getBytes()), Map.class);

		assertThat("measureId should be %s" + MEASURE_ID, content.get("measureId"), is(MEASURE_ID));
		assertThat("value should be " + VALUE, content.get("value"), is(VALUE));
	}

	@Test
	public void testInternalEncode(){
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder();

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertThat("The value must be" + VALUE, jsonWrapper.getString("value"), is(VALUE));
	}

	@Test
	public void testInternalEncodeNoChildNoValue(){
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder();
		aciMeasurePerformedRnR.addChildNodes();

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertThat("The measureId must be " + MEASURE_ID, jsonWrapper.getString("measureId"), is(MEASURE_ID));
		assertEquals("There should be no " + VALUE, jsonWrapper.getString("value"), null);
	}

}
