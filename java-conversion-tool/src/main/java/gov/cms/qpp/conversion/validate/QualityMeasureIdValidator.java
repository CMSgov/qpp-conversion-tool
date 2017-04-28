package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

@Validator(templateId = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdValidator extends NodeValidator{
	@Override
	protected void internalValidateSingleNode(final Node node) {

	}

	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {

	}
}
