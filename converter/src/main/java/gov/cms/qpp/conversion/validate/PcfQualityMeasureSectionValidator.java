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
		"2c928084-83d3-1b44-0183-eb75dc8a03db", // 122v12
		"2c928084-82ea-d7c5-0183-6bf2944520dc", // 130v12
		"2c928085-806c-39a2-0180-7092fa9b0145"  // 165v12
	};

	static final String[] PCF_MEASURE_IDS = {
		"122v12",
		"130v12",
		"165v12"
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
