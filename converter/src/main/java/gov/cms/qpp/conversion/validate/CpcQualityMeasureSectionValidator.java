package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Validates a measure groupings for a CPC+ Quality Measure Section node.
 */
@Validator(value = TemplateId.MEASURE_SECTION_V2, program = Program.CPC)
public class CpcQualityMeasureSectionValidator extends NodeValidator {

	/**
	 * Validate that the Quality Measure Section contains an acceptable combination of measures...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		Checker checker = check(node);

		Arrays.stream(CpcGroupMinimum.values())
				.forEach(group -> checkGroupMinimum(checker, group));
		verifyOverallCount(checker);
	}

	private void checkGroupMinimum(Checker checker, CpcGroupMinimum groupMinimum) {
		String[] measureIds = grabGroupMeasures(groupMinimum);
		checker.hasMeasures(
				groupMinimum.makeError(measureIds), groupMinimum.minimum, measureIds);
	}

	String[] grabGroupMeasures(CpcGroupMinimum groupMinimum) {
		Map<String, List<MeasureConfig>> cpcPlusGroups = MeasureConfigs.getCpcPlusGroups();

		return cpcPlusGroups.get(groupMinimum.name()).stream()
//				.map(MeasureConfig::getMeasureId)
				.map(MeasureConfig::getElectronicMeasureVerUuid)
				.toArray(String[]::new);
	}

	private void verifyOverallCount(Checker checker) {
		String[] measureIds = MeasureConfigs.getCpcPlusGroups()
				.values().stream()
				.flatMap(List::stream)
//				.map(MeasureConfig::getMeasureId)
				.map(MeasureConfig::getElectronicMeasureVerUuid)
				.toArray(String[]::new);

		checker.hasMeasures(
				CpcGroupMinimum.makeOverallError(measureIds), CpcGroupMinimum.NUMBER_OF_MEASURES_REQUIRED, measureIds);
	}

	enum CpcGroupMinimum {
		A("outcome", 2),
		B("complex process", 2);

		private static int NUMBER_OF_MEASURES_REQUIRED = 9;
		private String label;
		private int minimum;

		CpcGroupMinimum(String label, int minimum) {
			this.label = label;
			this.minimum = minimum;
		}

		static LocalizedError makeOverallError(String... measureIds) {
			return ErrorCode.CPC_PLUS_TOO_FEW_QUALITY_MEASURES
					.format(CpcGroupMinimum.NUMBER_OF_MEASURES_REQUIRED, String.join(",", measureIds));
		}

		LocalizedError makeError(String... measureIds) {
			return ErrorCode.CPC_PLUS_TOO_FEW_QUALITY_MEASURE_CATEGORY
					.format(minimum, label, String.join(",", measureIds));
		}
	}
}
