package gov.cms.qpp.conversion.encode.helper;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Map;
import java.util.Optional;

/**
 * This class helps get the Reporting Parameters out of the Clinical Document Encoder
 */
public class ReportingParameters {

	/**
	 * The Clinical Document contains a Reporting Parameters Section,
	 * Several encoders want access to the reporting node.
	 * @param childMapByTemplateId List of nodes for the children of Clinical Document
	 * @return Optional Node  The Reporting Parameter Node if present
	 */
	public static Optional<Node> getReportingNode(Map<String, Node> childMapByTemplateId){

		Node nullableReportingNode =
				childMapByTemplateId.remove(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		Optional<Node> reportingNode
				= Optional.ofNullable(nullableReportingNode)
				.flatMap(rp -> rp.getChildNodes().stream().findFirst());
		return reportingNode;
	}
}
