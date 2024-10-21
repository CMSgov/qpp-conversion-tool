package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.util.NodeHelper;

import java.util.Locale;

import static gov.cms.qpp.conversion.model.Constants.*;

@Validator(value = TemplateId.REPORTING_PARAMETERS_ACT, program = Program.PCF)
public class PcfPerformancePeriodValidation extends NodeValidator {

	private static final String REPORTING_PERIOD_START = Context.REPORTING_YEAR + "0101";
	private static final String REPORTING_PERIOD_END = Context.REPORTING_YEAR + "1231";

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		Node clinicalDocument = NodeHelper.findParent(node, TemplateId.CLINICAL_DOCUMENT);
		String programName = clinicalDocument.getValue(PROGRAM_NAME).toUpperCase(Locale.ROOT);

		checkErrors(node)
			.valueIs(ProblemCode.PCF_PERFORMANCE_PERIOD_START.format(programName),
				PERFORMANCE_START, REPORTING_PERIOD_START)
			.valueIs(ProblemCode.PCF_PERFORMANCE_PERIOD_END.format(programName),
				PERFORMANCE_END, REPORTING_PERIOD_END);
	}
}
