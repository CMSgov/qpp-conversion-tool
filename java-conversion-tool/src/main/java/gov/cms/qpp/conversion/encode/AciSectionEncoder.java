package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;

import java.util.List;

import static gov.cms.qpp.conversion.Converter.CLIENT_LOG;

/**
 * Encoder to serialize ACI Section and it's measures
 */
@Encoder(TemplateId.ACI_SECTION)
public class AciSectionEncoder extends QppOutputEncoder {

	/**
	 *  Encodes an ACI Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the ACI Section
	 * @param node Node that represents the ACI Section
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putString("category", node.getValue("category"));
		wrapper.putString("submissionMethod", "electronicHealthRecord");
		List<Node> children = node.getChildNodes();
		JsonWrapper measurementsWrapper = new JsonWrapper();

		encodeChildren(children, measurementsWrapper);

		wrapper.putObject("measurements", measurementsWrapper);

		encodeReportingParameter(wrapper, node);
	}

	/**
	 * Encodes the children of the given section
	 *
	 * @param children child nodes of the given section
	 * @param measurementsWrapper wrapper that holds the measurements of a section
	 */
	private void encodeChildren(List<Node> children, JsonWrapper measurementsWrapper) {
		JsonWrapper childWrapper;
		for (Node currentChild : children) {
			childWrapper = new JsonWrapper();
			TemplateId templateId = currentChild.getType();
			if (TemplateId.REPORTING_PARAMETERS_ACT != templateId) {
				JsonOutputEncoder childEncoder = ENCODERS.get(templateId);

				if (childEncoder != null) {
					childEncoder.encode(childWrapper, currentChild);
					measurementsWrapper.putObject(childWrapper);
				} else {
					addValidationError(new Detail("Failed to find an AciSectionEncoder", currentChild.getPath()));
				}
			}
		}
	}

	/**
	 * Encodes the reporting parameter section
	 *
	 * @param wrapper wrapper that holds the section
	 * @param node ACI Section Node
	 */
	private void encodeReportingParameter(JsonWrapper wrapper, Node node) {
		JsonOutputEncoder reportingParamEncoder = ENCODERS.get(TemplateId.REPORTING_PARAMETERS_ACT);
		Node reportingChild = node.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
		if (reportingChild == null) {
			CLIENT_LOG.error("Missing Reporting Parameters from ACI Section");
			return;
		}
		reportingParamEncoder.encode(wrapper, reportingChild);
		maintainContinuity(wrapper, reportingChild, ReportingParametersActDecoder.PERFORMANCE_END);
		maintainContinuity(wrapper, reportingChild, ReportingParametersActDecoder.PERFORMANCE_START);
	}
}
