package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Encoder to serialize Quality Section  (eCQM) and it's measures
 */
@Encoder(TemplateId.MEASURE_SECTION_V2)
public class QualitySectionEncoder extends QppOutputEncoder {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QualitySectionEncoder.class);
	private static final String CATEGORY = "category";
	private static final String SUBMISSION_METHOD = "submissionMethod";

	/**
	 * Encodes an Quality Section into the QPP format
	 *
	 * @param wrapper JsonWrapper that will represent the Quality Section
	 * @param node    Node that represents the Quality Section and its measurements children if any
	 * @throws EncodeException If an error occurs during encoding
	 */
	@Override
	public void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		wrapper.putString(CATEGORY, node.getValue(CATEGORY));
		wrapper.putString(SUBMISSION_METHOD, node.getValue(SUBMISSION_METHOD));

		List<Node> children = node.getChildNodes();
		JsonWrapper measurementsWrapper = new JsonWrapper();

		encodeChildren(children, measurementsWrapper);
		wrapper.putObject("measurements", measurementsWrapper);
	}

	private void encodeChildren(List<Node> children, JsonWrapper measurementsWrapper) throws EncodeException {
		JsonWrapper childWrapper;
		for (Node currentChild : children) {
			childWrapper = new JsonWrapper();
			String templateId = currentChild.getId();
			JsonOutputEncoder childEncoder = ENCODERS.get(templateId);

			if (childEncoder == null) {
				String msg = "Failed to find an encoder for template " + currentChild.getType().toString();
				DEV_LOG.error(msg);
				throw new EncodeException(msg);
			} else {
				childEncoder.encode(childWrapper, currentChild);
				measurementsWrapper.putObject(childWrapper);
			}
		}
	}
}
