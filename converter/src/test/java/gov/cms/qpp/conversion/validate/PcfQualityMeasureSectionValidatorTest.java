package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class PcfQualityMeasureSectionValidatorTest {

	private PcfQualityMeasureSectionValidator validator;

	@BeforeAll
	static void setup() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@BeforeEach
	void setupTest() {
		validator = new PcfQualityMeasureSectionValidator();
	}

	@Test
	void testMissingPcfExpectedMeasureIds() {
		Node node = new Node();
		LocalizedProblem message = ProblemCode.PCF_TOO_FEW_QUALITY_MEASURE_CATEGORY
			.format(3, String.join(",", PcfQualityMeasureSectionValidator.PCF_MEASURE_IDS));
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(message);
	}
	@Test
	void testCorrectPcfExpectedMeasureIds() {
		Node node = setupMeasures(PcfQualityMeasureSectionValidator.PCF_REQUIRED_MEASURES);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).hasSize(0);
	}

	private Node setupMeasures(String[]... measureIds) {
		Node[] nodes = Arrays.stream(measureIds)
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
