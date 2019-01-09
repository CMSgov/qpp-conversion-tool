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
	private String[] groupAmeasures = {"40280382-5abd-fa46-015b-49abb28d38b2"
		,"40280382-5abd-fa46-015b-4981e40b37e6"};
	private String[] groupBmeasures = {"40280382-5abd-fa46-015b-4993577e382e",
		"40280382-5abd-fa46-015b-49956e7c383a",
		"40280382-5b4d-eebc-015b-5844953b00a3",
		"40280382-5abd-fa46-015b-49b36bf238d7",
		"40280382-5abd-fa46-015b-499c87c0385e",
		"40280382-5971-4eed-015a-5c465a344ded",
		"40280382-5b4d-eebc-015b-5d99505001ea",
		"40280382-5abd-fa46-015b-1b7c6bb929d0",
		"40280382-5abd-fa46-015b-49b5b1e638e3",
		"40280382-5abd-fa46-015b-49973dc03846",
		"40280382-5abd-fa46-015b-498df1243816",
		"40280382-5b4d-eebc-015b-8245e0fa06b7",
		"40280382-5b4d-eebc-015b-5ea9efcc02ac",
		"40280382-5abd-fa46-015b-4989b55937fe",
		"40280382-5b4d-eebc-015b-5e87add90267",
		"40280382-5abd-fa46-015b-49a7a51f38a0",
		"40280382-5971-4eed-015a-4e002d4b4b66"};

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
	void missingGroupBmeasures() {
		Node node = setupMeasures(groupAmeasures);
		LocalizedError message = CpcGroupMinimum.OTHER_MEASURE.makeError(groupBmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
	}

	@Test
	void tooFewBmeasures() {
		Node node = setupMeasures(groupAmeasures, new String[] {groupBmeasures[0]});
		LocalizedError message = CpcGroupMinimum.OTHER_MEASURE.makeError(groupBmeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
	}

	@Test
	void tooFewOverallmeasures() {
		Node node = setupMeasures(groupAmeasures, new String[] {groupBmeasures[0], groupBmeasures[1]});
		LocalizedError message = CpcGroupMinimum.makeOverallError(overallMeasures);
		List<Detail> details = validator.validateSingleNode(node).getErrors();
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(message);
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
