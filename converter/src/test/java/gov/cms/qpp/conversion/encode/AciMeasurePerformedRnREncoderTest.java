package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.util.JsonHelper;

class AciMeasurePerformedRnREncoderTest {

	private static final String MEASURE_ID = "ACI_INFBLO_1";
	private static final String VALUE = "Y";

	private List<Node> nodes;
	private Node aciMeasurePerformedRnR;
	private Node measurePerformed;

	@BeforeEach
	void createNode() {
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
	void testEncoder() throws IOException {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}

		Map<String, Object> content = JsonHelper.readJson(new ByteArrayInputStream(sw.toString().getBytes()), Map.class);

		assertWithMessage("MeasureId should be %s", MEASURE_ID)
				.that(content.get("measureId"))
				.isEqualTo(MEASURE_ID);
		assertWithMessage("value should be true")
				.that(content.get("value"))
				.isEqualTo(true);
	}

	@Test
	void testInternalEncode() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(new Context());

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertWithMessage("MeasureId should be %s", MEASURE_ID)
				.that(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertWithMessage("value should be true")
				.that(jsonWrapper.getBoolean("value"))
				.isEqualTo(true);
	}

	@Test
	void testInternalEncodeNoChildNoValue(){
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(new Context());
		aciMeasurePerformedRnR.setChildNodes();

		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertWithMessage("MeasureId should be %s", MEASURE_ID)
				.that(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertWithMessage("Value must be null")
				.that(jsonWrapper.getBoolean("value"))
				.isNull();
	}

	@Test
	void testInternalEncodeBooleanTrueValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(new Context());
		aciMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed", "Y");
		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertWithMessage("MeasureId should be %s", MEASURE_ID)
				.that(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertWithMessage("value should be true")
				.that(jsonWrapper.getBoolean("value"))
				.isEqualTo(true);
	}

	@Test
	void testInternalEncodeBooleanFalseValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(new Context());
		aciMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed","N");
		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertWithMessage("MeasureId should be %s", MEASURE_ID)
				.that(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertWithMessage("value should be false")
				.that(jsonWrapper.getBoolean("value"))
				.isEqualTo(false);
	}

	@Test
	void testInternalEncodeBooleanStringValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		AciMeasurePerformedRnREncoder objectUnderTest = new AciMeasurePerformedRnREncoder(new Context());
		String unknownValue = "Some unknown value";
		aciMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed",unknownValue);
		//execute
		objectUnderTest.internalEncode(jsonWrapper, aciMeasurePerformedRnR);

		//assert
		assertWithMessage("MeasureId should be %s", MEASURE_ID)
				.that(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertWithMessage("value should be a string value")
				.that(jsonWrapper.getString("value"))
				.isEqualTo(unknownValue);
	}
}
