package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;

/**
 * Validates a measure groupings for a CPC+ Quality Measure Section node.
 */
@Validator(value = TemplateId.MEASURE_SECTION_V4, program = Program.PCF)
public class PcfQualityMeasureSectionValidator extends NodeValidator {

	// To Do: update when Measures data gets updated
	public static final String[] PCF_REQUIRED_MEASURES = {
		"40280382-6963-bf5e-0169-da3833273869", // 122v8
		"40280382-6963-bf5e-0169-da566ea338a5", // 130v8
		"40280382-6963-bf5e-0169-da5e74be38bf"  // 165v8
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
	 * Verify that CPC+ measurement group minimums are met.
	 * @param checker node validator helper
	 */
	void checkGroupMinimum(Checker checker) {
		checker.hasMeasures(makeError(PCF_REQUIRED_MEASURES), PCF_REQUIRED_MEASURES);
	}

	LocalizedProblem makeError(String... measureIds) {
		return ProblemCode.CPC_PLUS_TOO_FEW_QUALITY_MEASURE_CATEGORY
			.format(3, "PCF", String.join(",", measureIds));
	}
}
