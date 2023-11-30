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
		"2c928085-7b2a-eb52-017b-56761e0218d0", // 122v11
		"2c928083-7ace-2267-017b-11fbb9c913c4", // 130v11
		"2c928082-7a14-d92c-017a-67b6f9971ea8"  // 165v11
	};

	static final String[] PCF_MEASURE_IDS = {
		"122v11",
		"130v11",
		"165v11"
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
