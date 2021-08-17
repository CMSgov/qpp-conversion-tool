package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.validate.CpcQualityMeasureSectionValidator.CpcGroupMinimum;
import gov.cms.qpp.test.enums.EnumContract;

class CpcQualityMeasureSectionValidatorTest {

	private CpcQualityMeasureSectionValidator validator;
	private String[] groupAmeasures = {"2c928085-7198-38ee-0171-9d78a0d406b3", "2c928085-7198-38ee-0171-9da6456007ab"};
	private String[] groupBmeasures = {};

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
		LocalizedProblem message = CpcGroupMinimum.OUTCOME_MEASURE.makeError(groupAmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
	}

	@Test
	void tooFewGroupAmeasures() {
		Node node = setupMeasures(new String[] {groupAmeasures[0]});
		LocalizedProblem message = CpcGroupMinimum.OUTCOME_MEASURE.makeError(groupAmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
	}

	@Test
	void missingGroupBmeasuresNoLongerFails() {
		Node node = setupMeasures(groupAmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).isEmpty();
	}

	@Test
	void justRight() {
		Node node = setupMeasures(groupAmeasures, groupBmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).hasSize(0);
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

	@Nested
	static class CpcGroupMinimumTest implements EnumContract {

		@Override
		public Class<? extends Enum<?>> getEnumType() {
			return CpcGroupMinimum.class;
		}

	}
}
