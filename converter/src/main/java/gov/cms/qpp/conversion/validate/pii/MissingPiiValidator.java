package gov.cms.qpp.conversion.validate.pii;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum MissingPiiValidator implements PiiValidator {

	INSTANCE;

	@Override
	public void validateApmTinNpiCombination(Node node, NodeValidator validator) {
		final Logger DEV_LOG = LoggerFactory.getLogger(QrdaValidator.class);
		DEV_LOG.info("Inside Missing PII Validator - Validate APM TIN NPI Combination function");
		validator.addWarning(Detail.forProblemAndNode(ProblemCode.MISSING_PII_VALIDATOR, node));
	}

}
