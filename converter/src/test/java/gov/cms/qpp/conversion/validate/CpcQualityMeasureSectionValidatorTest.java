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

import static com.google.common.truth.Truth.assertThat;

class CpcQualityMeasureSectionValidatorTest {

	private CpcQualityMeasureSectionValidator validator;
	private String[] groupAmeasures = {"236", "001", "370"};
	private String[] groupBmeasures = {"318", "281", "305", "238"};
	private String[] overallMeasures = {"236", "001", "370", "318", "281", "305",
			"238", "312", "374", "113", "112", "117", "309", "226"};

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
		Node node = setupMeasures(new String[] {"236"});
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
