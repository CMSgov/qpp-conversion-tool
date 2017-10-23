package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Encoder to serialize ACI Section and it's measures
 */
@Encoder(TemplateId.ACI_SECTION)
public class AciSectionEncoder extends QppOutputEncoder {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(AciSectionEncoder.class);
	public static final String SUBMISSION_METHOD = "submissionMethod";

	public AciSectionEncoder(Context context) {
		super(context);
	}

	/**
	 *  Encodes an ACI Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the ACI Section
	 * @param node Node that represents the ACI Section
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		encodeTopLevelValues(wrapper, node);
		List<Node> children = node.getChildNodes();
		JsonWrapper measurementsWrapper = new JsonWrapper();

		encodeChildren(children, measurementsWrapper);

		wrapper.putObject("measurements", measurementsWrapper);

		encodeReportingParameter(wrapper, node);
	}

	private void encodeTopLevelValues(JsonWrapper wrapper, Node node) {
		wrapper.putString("category", node.getValue("category"));
		wrapper.putString(SUBMISSION_METHOD, "electronicHealthRecord");
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
				JsonOutputEncoder childEncoder = encoders.get(templateId);

				if (childEncoder != null) {
					childEncoder.encode(childWrapper, currentChild);
					measurementsWrapper.putObject(childWrapper);
				} else {
					Detail detail = Detail.forErrorCode(ErrorCode.ENCODER_MISSING);
					detail.setTemplateId(currentChild.getType());
					detail.setPath(currentChild.getPath());
					addValidationError(detail);
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
		JsonOutputEncoder reportingParamEncoder = encoders.get(TemplateId.REPORTING_PARAMETERS_ACT);
		Node reportingChild = node.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
		if (reportingChild == null) {
			DEV_LOG.error("Missing Reporting Parameters from ACI Section");
			return;
		}
		reportingParamEncoder.encode(wrapper, reportingChild);
		maintainContinuity(wrapper, reportingChild, ReportingParametersActDecoder.PERFORMANCE_END);
		maintainContinuity(wrapper, reportingChild, ReportingParametersActDecoder.PERFORMANCE_START);
	}
}
