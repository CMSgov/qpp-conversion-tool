package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Top level Encoder for serializing into QPP format.
 */
public class QppOutputEncoder extends JsonOutputEncoder {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QppOutputEncoder.class);
	protected final Registry<JsonOutputEncoder> encoders;
	protected final Context context;

	public QppOutputEncoder(Context context) {
		// Defensive-copy the incoming Context
		Context ctxCopy = new Context();
		ctxCopy.setDoValidation(context.isDoValidation());
		ctxCopy.setHistorical(context.isHistorical());
		this.context = ctxCopy;

		this.encoders = this.context.getRegistry(Encoder.class);
	}

	@Override
	public final void encode(JsonWrapper wrapper, Node node) {
		DEV_LOG.debug("Using {} to encode {}", this.getClass().getName(), node);
		super.encode(wrapper, node);
	}

	/**
	 * Top level internalEncode that calls its children from the registry.
	 * Each encoder calls its child encoder with an encode() method
	 *
	 * @param wrapper object to encode into
	 * @param node    object to encode
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		JsonOutputEncoder encoder = encoders.get(node.getType());
		if (encoder != null) {
			// Pass a defensive copy of context if used internally
			encoder.encode(wrapper, node);
		}
	}

	/**
	 * Provide a means to associate JSON path to XPath expression when data is harvested from nodes by means
	 * other than extraction via the node's specified {@link Encoder}.
	 *
	 * @param wrapper   parent {@link JsonWrapper}
	 * @param node      decoded QRDA node
	 * @param leafLabel encoded JSON attribute name
	 */
	void maintainContinuity(JsonWrapper wrapper, Node node, String leafLabel) {
		wrapper.attachMetadata(node, leafLabel);
	}
}
