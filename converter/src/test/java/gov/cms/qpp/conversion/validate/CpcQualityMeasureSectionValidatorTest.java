package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.validate.CpcQualityMeasureSectionValidator.CpcGroupMinimum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.google.common.truth.Truth.assertThat;

class CpcQualityMeasureSectionValidatorTest {

	private CpcQualityMeasureSectionValidator validator;
	private String[] groupAmeasures = {"40280381-51f0-825b-0152-22b98cff181a",
			"40280381-51f0-825b-0152-229afff616ee",
			"40280381-5118-2f4e-0151-3a9382cd09ba"};
	private String[] groupBmeasures = {"40280381-51f0-825b-0152-22aae8a21778",
			"40280381-52fc-3a32-0153-1a401cc10b57",
			"40280381-528a-60ff-0152-8e089ed20376",
			"40280381-52fc-3a32-0153-56d2b4f01ae5"};
	private String[] groupCmeasures = {"40280381-51f0-825b-0152-22ba7621182e",
			"40280381-5118-2f4e-0151-59fb81bf1055",
			"40280381-51f0-825b-0152-22a1e7e81737",
			"40280381-51f0-825b-0152-229c4ea3170c",
			"40280381-51f0-825b-0152-22a24cdd1740",
			"40280381-51f0-825b-0152-229bdcab1702",
			"40280381-503f-a1fc-0150-d33f5b0a1b8c"};

	private String[] overallMeasures =
			Stream.of(groupAmeasures, groupBmeasures, groupCmeasures).flatMap(Stream::of)
					.toArray(String[]::new);

	@BeforeAll
	static void setup() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@BeforeEach
	void setupTest() {
		validator = new CpcQualityMeasureSectionValidator();
	}

	@Test
	void missingGroupAmeasures() {
		Node node = new Node();
		LocalizedError message = CpcGroupMinimum.A.makeError(groupAmeasures);
		validator.internalValidateSingleNode(node);
		assertThat(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	@Test
	void tooFewGroupAmeasures() {
		Node node = setupMeasures(new String[] {"40280381-51f0-825b-0152-22b98cff181a"});
		LocalizedError message = CpcGroupMinimum.A.makeError(groupAmeasures);
		validator.internalValidateSingleNode(node);
		assertThat(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	@Test
	void missingGroupBmeasures() {
		Node node = setupMeasures(groupAmeasures);
		LocalizedError message = CpcGroupMinimum.B.makeError(groupBmeasures);
		validator.internalValidateSingleNode(node);
		assertThat(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	@Test
	void tooFewBmeasures() {
		Node node = setupMeasures(groupAmeasures, new String[] {"318"});
		LocalizedError message = CpcGroupMinimum.B.makeError(groupBmeasures);
		validator.internalValidateSingleNode(node);
		assertThat(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	@Test
	void tooFewOverallmeasures() {
		Node node = setupMeasures(groupAmeasures, groupBmeasures);
		LocalizedError message = CpcGroupMinimum.makeOverallError(overallMeasures);
		validator.internalValidateSingleNode(node);
		assertThat(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	@Test
	void justRight() {
		Node node = setupMeasures(groupAmeasures, groupBmeasures, new String[]{groupCmeasures[0], groupCmeasures[1]});
		validator.internalValidateSingleNode(node);
		assertThat(validator.getDetails()).hasSize(0);
	}

	private Node setupMeasures(String[]... measureGroups) {
		Node[] nodes = Arrays.stream(measureGroups)
				.flatMap(Arrays::stream)
				.map(this::mockMeasureNode)
				.toArray(Node[]::new);
		Node node = new Node();
		node.addChildNodes(nodes);
		return node;
	}

	private Node mockMeasureNode(String measureId) {
		Node node = new Node();
		node.putValue("measureId", measureId);
		return node;
	}
}
