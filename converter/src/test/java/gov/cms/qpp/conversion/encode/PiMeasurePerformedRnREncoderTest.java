package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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

class PiMeasurePerformedRnREncoderTest {

	private static final String MEASURE_ID = "ACI_INFBLO_1";
	private static final String VALUE = "Y";

	private List<Node> nodes;
	private Node piMeasurePerformedRnR;
	private Node measurePerformed;

	@BeforeEach
	void createNode() {
		piMeasurePerformedRnR = new Node(TemplateId.PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		piMeasurePerformedRnR.putValue("measureId", MEASURE_ID);

		measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformed.putValue("measurePerformed", VALUE);

		piMeasurePerformedRnR.addChildNode(measurePerformed);

		nodes = new ArrayList<>();
		nodes.add(piMeasurePerformedRnR);
	}

	@Test
	@SuppressWarnings("unchecked")
	void testEncoder() {
		QppOutputEncoder encoder = new QppOutputEncoder(new Context());
		encoder.setNodes(nodes);

		StringWriter sw = new StringWriter();

		try {
			encoder.encode(new BufferedWriter(sw));
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}

		Map<String, Object> content = JsonHelper.readJson(new ByteArrayInputStream(sw.toString().getBytes()), Map.class);

		assertThat(content.get("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(content.get("value"))
				.isEqualTo(true);
	}

	@Test
	void testInternalEncode() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiMeasurePerformedRnREncoder objectUnderTest = new PiMeasurePerformedRnREncoder(new Context());

		//execute
		objectUnderTest.internalEncode(jsonWrapper, piMeasurePerformedRnR);

		//assert
		assertThat(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(jsonWrapper.getBoolean("value"))
				.isEqualTo(true);
	}

	@Test
	void testInternalEncodeNoChildNoValue(){
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiMeasurePerformedRnREncoder objectUnderTest = new PiMeasurePerformedRnREncoder(new Context());
		piMeasurePerformedRnR.setChildNodes();

		//execute
		objectUnderTest.internalEncode(jsonWrapper, piMeasurePerformedRnR);

		//assert
		assertThat(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(jsonWrapper.getBoolean("value"))
				.isNull();
	}

	@Test
	void testInternalEncodeBooleanTrueValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiMeasurePerformedRnREncoder objectUnderTest = new PiMeasurePerformedRnREncoder(new Context());
		piMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed", "Y");
		//execute
		objectUnderTest.internalEncode(jsonWrapper, piMeasurePerformedRnR);

		//assert
		assertThat(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(jsonWrapper.getBoolean("value"))
				.isEqualTo(true);
	}

	@Test
	void testInternalEncodeBooleanFalseValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiMeasurePerformedRnREncoder objectUnderTest = new PiMeasurePerformedRnREncoder(new Context());
		piMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed","N");
		//execute
		objectUnderTest.internalEncode(jsonWrapper, piMeasurePerformedRnR);

		//assert
		assertThat(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(jsonWrapper.getBoolean("value"))
				.isEqualTo(false);
	}

	@Test
	void testInternalEncodeBooleanStringValue() {
		//set-up
		JsonWrapper jsonWrapper = new JsonWrapper();
		PiMeasurePerformedRnREncoder objectUnderTest = new PiMeasurePerformedRnREncoder(new Context());
		String unknownValue = "Some unknown value";
		piMeasurePerformedRnR.getChildNodes().get(0).putValue("measurePerformed",unknownValue);
		//execute
		objectUnderTest.internalEncode(jsonWrapper, piMeasurePerformedRnR);

		//assert
		assertThat(jsonWrapper.getString("measureId"))
				.isEqualTo(MEASURE_ID);
		assertThat(jsonWrapper.getString("value"))
				.isEqualTo(unknownValue);
	}
}
