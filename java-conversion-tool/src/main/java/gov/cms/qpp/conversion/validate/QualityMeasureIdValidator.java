package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator(templateId = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdValidator extends NodeValidator {
	@Override
	protected void internalValidateSingleNode(final Node node) {
		check(node)
			.value("The measure reference results must have a measure GUID", "measureId")
			.childMinimum("The measure reference results must have at least one measure", 1, TemplateId.MEASURE_DATA_CMS_V2);
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations required
	}
}
