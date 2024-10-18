package gov.cms.qpp.conversion.validate.pii;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.validate.NodeValidator;

public enum MissingPiiValidator implements PiiValidator {

	INSTANCE;

	@Override
	public void validateApmTinNpiCombination(Node node, NodeValidator validator) {
		validator.addWarning(Detail.forProblemAndNode(ProblemCode.MISSING_PII_VALIDATOR, node));
	}

}
