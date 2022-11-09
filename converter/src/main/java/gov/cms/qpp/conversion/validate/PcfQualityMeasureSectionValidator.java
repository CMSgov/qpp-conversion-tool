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
@Validator(value = TemplateId.MEASURE_SECTION_V4, program = Program.PCF)
public class PcfQualityMeasureSectionValidator extends NodeValidator {

	// To Do: Update to Y6 Measure UUID(s) when Measures data gets updated
	static final String[] PCF_REQUIRED_MEASURES = {
		"2c928082-74c2-3313-0174-c60bd07b02a6", // 122v10
		"2c928082-74c2-3313-0174-daf39f2c0658", // 130v10
		"2c928082-7505-caf9-0175-2382d1bd06b1"  // 165v10
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
 		checker.hasMeasures(makeError(PCF_REQUIRED_MEASURES), PCF_REQUIRED_MEASURES);
	}

	LocalizedProblem makeError(String... measureIds) {
		return ProblemCode.PCF_TOO_FEW_QUALITY_MEASURE_CATEGORY
			.format(3, String.join(",", measureIds));
	}
}
