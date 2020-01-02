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
@Validator(value = TemplateId.MEASURE_SECTION_V3, program = Program.CPC)
public class CpcQualityMeasureSectionValidator extends NodeValidator {

	/**
	 * Validate that the Quality Measure Section contains an acceptable combination of measures...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		Checker checker = forceCheckErrors(node);

		Arrays.stream(CpcGroupMinimum.values())
				.forEach(group -> checkGroupMinimum(checker, group));
	}

	/**
	 * Verify that CPC+ measurement group minimums are met.
	 * @param checker node validator helper
	 * @param groupMinimum group minimum config
	 */
	private void checkGroupMinimum(Checker checker, CpcGroupMinimum groupMinimum) {
		String[] measureIds = grabGroupMeasures(groupMinimum);
		checker.hasMeasures(
				groupMinimum.makeError(measureIds), groupMinimum.minimum, measureIds);
	}

	/**
	 * Retrieve measure ids for group specific measures.
	 * @param groupMinimum group config
	 * @return measure id array
	 */
	String[] grabGroupMeasures(CpcGroupMinimum groupMinimum) {
		Map<String, List<MeasureConfig>> cpcPlusGroups = MeasureConfigs.getCpcPlusGroups();

		return cpcPlusGroups.get(groupMinimum.getMapName()).stream()
				.map(MeasureConfig::getElectronicMeasureVerUuid)
				.toArray(String[]::new);
	}

	/**
	 * A holder of CPC+ group specific configuration information.
	 */
	enum CpcGroupMinimum {
		OUTCOME_MEASURE("Outcome_Measure", "outcome", 2),
		OTHER_MEASURE("Other_Measure", "other", 0);

		private static final int NUMBER_OF_MEASURES_REQUIRED = 2;
		private String mapName;
		private String label;
		private int minimum;

		CpcGroupMinimum(String mapName, String label, int minimum) {
			this.label = label;
			this.minimum = minimum;
			this.mapName = mapName;
		}

		public String getMapName() {
			return mapName;
		}

		LocalizedError makeError(String... measureIds) {
			return ErrorCode.CPC_PLUS_TOO_FEW_QUALITY_MEASURE_CATEGORY
					.format(minimum, label, String.join(",", measureIds));
		}
	}
}
