package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;

/**
 * Validates a measure groupings for a PCF Quality Measure Section node.
 */
@Validator(value = TemplateId.MEASURE_SECTION_V5, program = Program.PCF)
public class PcfQualityMeasureSectionValidator extends NodeValidator {

	static final String[] PCF_REQUIRED_MEASURES = {
		"2c928083-8907-ce68-0189-2bc5fa0d0739", // 122v13
		"2c928083-8907-ce68-0189-2bc134cf06bb", // 130v13
		"2c928083-8907-ce68-0189-2bbd31d6064e"  // 165v13
	};

	static final String[] PCF_MEASURE_IDS = {
		"122v13",
		"130v13",
		"165v13"
	};

	/**
	 * Validate that the Quality Measure Section contains an acceptable combination of measures...
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		Checker checker = forceCheckErrors(node);

		checkGroupMinimum(checker);
	}

	/**
	 * Verify that PCF measurement group minimums are met.
	 * @param checker node validator helper
	 */
	void checkGroupMinimum(Checker checker) {
 		checker.hasMeasures(makeError(PCF_MEASURE_IDS), PCF_REQUIRED_MEASURES);
	}

	LocalizedProblem makeError(String... measureIds) {
		return ProblemCode.PCF_TOO_FEW_QUALITY_MEASURE_CATEGORY
			.format(3, String.join(",", measureIds));
	}
}
