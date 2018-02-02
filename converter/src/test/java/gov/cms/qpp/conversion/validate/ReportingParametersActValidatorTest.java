package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.jupiter.api.Test;

class ReportingParametersActValidatorTest {
	private Node reportingParametersActNode;

	@Test
	void testReportingParametersActValidDateSuccess() {
		reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		
	}
}
