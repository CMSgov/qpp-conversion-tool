package gov.cms.qpp.conversion.encode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;

import java.util.List;
import java.util.Optional;

/**
 * Encoder to serialize PI Section and it's measures
 */
@Encoder(TemplateId.PI_SECTION_V2)
public class PiSectionEncoder extends QppOutputEncoder {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(PiSectionEncoder.class);
	public static final String SUBMISSION_METHOD = "submissionMethod";

	public PiSectionEncoder(Context context) {
		super(context);
	}

	/**
	 *  Encodes an PI Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the PI Section
	 * @param node Node that represents the PI Section
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) {
		encodeTopLevelValues(wrapper, node);
		List<Node> children = node.getChildNodes();
		JsonWrapper measurementsWrapper = new JsonWrapper();

		encodeChildren(children, measurementsWrapper);

		wrapper.put("measurements", measurementsWrapper);

		Optional.ofNullable(node.getParent()).ifPresent(parent -> pilferParent(wrapper, parent));
		encodeReportingParameter(wrapper, node);
	}

	private void encodeTopLevelValues(JsonWrapper wrapper, Node node) {
		wrapper.put("category", node.getValue("category"));
		wrapper.put(SUBMISSION_METHOD, "electronicHealthRecord");
		if (TemplateId.PI_SECTION_V2 == node.getType() && Program.isMips(node.getParent())) {
			wrapper.put(ClinicalDocumentDecoder.CEHRT, node.getParent().getValue(ClinicalDocumentDecoder.CEHRT));
		}
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
					measurementsWrapper.put(childWrapper);
				} else {
					addValidationError(Detail.forProblemAndNode(ProblemCode.ENCODER_MISSING, currentChild));
				}
			}
		}
	}

	/**
	 * Mine information from section parent.
	 *
	 * @param wrapper section's wrapper
	 * @param parent the clinical document node
	 */
	private void pilferParent(JsonWrapper wrapper, Node parent) {
		wrapper.put(ClinicalDocumentDecoder.PROGRAM_NAME,
				parent.getValue(ClinicalDocumentDecoder.PROGRAM_NAME));
		maintainContinuity(wrapper, parent, ClinicalDocumentDecoder.PROGRAM_NAME);
	}

	/**
	 * Encodes the reporting parameter section
	 *
	 * @param wrapper wrapper that holds the section
	 * @param node PI Section Node
	 */
	private void encodeReportingParameter(JsonWrapper wrapper, Node node) {
		JsonOutputEncoder reportingParamEncoder = encoders.get(TemplateId.REPORTING_PARAMETERS_ACT);
		Node reportingChild = node.findFirstNode(TemplateId.REPORTING_PARAMETERS_ACT);
		if (reportingChild == null) {
			DEV_LOG.error("Missing Reporting Parameters from PI Section");
			return;
		}
		reportingParamEncoder.encode(wrapper, reportingChild, false);
		maintainContinuity(wrapper, reportingChild, ReportingParametersActDecoder.PERFORMANCE_END);
		maintainContinuity(wrapper, reportingChild, ReportingParametersActDecoder.PERFORMANCE_START);
	}
}
