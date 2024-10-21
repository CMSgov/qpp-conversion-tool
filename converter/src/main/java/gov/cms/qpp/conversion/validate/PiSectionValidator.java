package gov.cms.qpp.conversion.validate;

import com.google.common.collect.Sets;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;

import java.util.Collections;
import java.util.Set;

import static gov.cms.qpp.conversion.model.Constants.MEASURE_ID;

/**
 * Validate the ACI Section.
 */
@Validator(TemplateId.PI_SECTION_V3)
public class PiSectionValidator extends NodeValidator {

	protected static final Set<String> RESTRICTED_PI_MEASURES = Collections.unmodifiableSet(Sets.newHashSet("PI_HIE_1", "PI_LVOTC_1", "PI_HIE_4", "PI_LVITC_2"));

	/**
	 * Validates the PI Section.
	 * <p>
	 * Validates the following.
	 * <ul>
	 * <li>One and only one reporting parameter exists.</li>
	 * </ul>
	 *
	 * @param node An ACI section node.
	 */
	@Override
	protected void performValidation(final Node node) {
		forceCheckErrors(node)
				.childExact(ProblemCode.PI_SECTION_MISSING_REPORTING_PARAMETER_ACT,
					1, TemplateId.REPORTING_PARAMETERS_ACT);

		validateMeasureRestrictions(node);
	}

	/**
	 * Validates that certain measures are not submitted with another
	 * Examples:
	 * PI_HIE_1 &&/|| PI_LVOTC_1 and PI_HIE_5
	 * PI_HIE_4 &&/|| PI_LVITC_2 and PI_HIE_5
	 *
	 * @param node the current PI section node
	 */
	private void validateMeasureRestrictions(Node node) {
		Node hie5Node = getNodeByMeasureId(node, Sets.newHashSet("PI_HIE_5"));
		if (hie5Node != null) {
			Node restrictedMeasure = getNodeByMeasureId(node, RESTRICTED_PI_MEASURES);
			if (restrictedMeasure != null) {
				LocalizedProblem problemCode = ProblemCode.PI_RESTRICTED_MEASURES;
				addError(Detail.forProblemAndNode(problemCode, node));
			}
		}
	}

	/**
	 * Find a specific node by measure id.
	 *
	 * @param node the current node holding a PI measure id.
	 * @param measureIds the Set of measures to search for.
	 * @return
	 */
	private Node getNodeByMeasureId(Node node, Set<String> measureIds) {
		return node.getChildNodes(TemplateId.PI_NUMERATOR_DENOMINATOR).filter(thisNode ->
			(measureIds.contains(thisNode.getValue(MEASURE_ID))))
			.findFirst().orElse(null);
	}
}
