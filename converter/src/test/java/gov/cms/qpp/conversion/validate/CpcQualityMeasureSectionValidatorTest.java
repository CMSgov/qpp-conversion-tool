package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.validate.CpcQualityMeasureSectionValidator.CpcGroupMinimum;
import gov.cms.qpp.test.enums.EnumContract;

class CpcQualityMeasureSectionValidatorTest {

	private CpcQualityMeasureSectionValidator validator;
	private String[] groupAmeasures = {"40280382-6258-7581-0162-9249c8ab1447",
		"40280382-6258-7581-0162-92d6e6db1680"};
	private String[] groupBmeasures = {"40280382-610b-e7a4-0161-9a6155603811",
		"40280382-6240-b6b9-0162-5096917708e0",
		"40280382-6258-7581-0162-92c7a9811647",
		"40280382-5fa6-fe85-0160-0ed2838423ca",
		"40280382-6258-7581-0162-927500b514ef",
		"40280382-6258-7581-0162-92877e281530",
		"40280382-6258-7581-0162-9208ce991364",
		"40280382-6258-7581-0162-92959376159d",
		"40280382-6240-b6b9-0162-5467c36a0b71",
		"40280382-6258-7581-0162-92106b67138d",
		"40280382-6258-7581-0162-aaad978c1b8b",
		"40280382-6240-b6b9-0162-54815a310c2c",
		"40280382-6258-7581-0162-63106f9201b2",
		"40280382-6258-7581-0162-92660f2414b9",
		"40280382-6258-7581-0162-9241a52a13fd",
		"40280382-6258-7581-0162-92a37a9b15df"};

	private String[] overallMeasures =
			Stream.of(groupBmeasures, groupAmeasures).flatMap(Stream::of)
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
		LocalizedError message = CpcGroupMinimum.OUTCOME_MEASURE.makeError(groupAmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
	}

	@Test
	void tooFewGroupAmeasures() {
		Node node = setupMeasures(new String[] {groupAmeasures[0]});
		LocalizedError message = CpcGroupMinimum.OUTCOME_MEASURE.makeError(groupAmeasures);
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
