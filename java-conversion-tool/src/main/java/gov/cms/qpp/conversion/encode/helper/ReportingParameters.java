package gov.cms.qpp.conversion.encode.helper;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.Map;
import java.util.Optional;

/**
 * This class helps get the Reporting Parameters out of the Clinical Document Encoder
 */
public interface ReportingParameters {

	/**
	 * The Clinical Document contains a Reporting Parameters Section,
	 * Several encoders want access to the reporting node.
	 *
	 * @param childMapByTemplateId List of nodes for the children of Clinical Document
	 * @return Optional Node  The Reporting Parameter Node if present
	 */
	static Optional<Node> getReportingNode(Map<String, Node> childMapByTemplateId) {
		Node nullableReportingNode =
				childMapByTemplateId.remove(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		return Optional.ofNullable(nullableReportingNode).flatMap(rp -> rp.getChildNodes().stream().findFirst());
	}
}
