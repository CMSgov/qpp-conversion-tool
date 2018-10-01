package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Top level Encoder for serializing into QPP format.
 */
public class QppOutputEncoder extends JsonOutputEncoder {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QppOutputEncoder.class);
	public static final String VALUE = "value";
	protected final Registry<JsonOutputEncoder> encoders;

	protected final Context context;

	public QppOutputEncoder(Context context) {
		this.context = context;
		this.encoders = context.getRegistry(Encoder.class);
	}

	@Override
	public final void encode(JsonWrapper wrapper, Node node) {
		DEV_LOG.debug("Using {} to encode {}", this.getClass().getName(), node);
		super.encode(wrapper, node);
	}

	/**
	 * Top level internalEncode that calls it's children from the registry.
	 * Each encoder calls its child encoder with an encode() method
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		JsonOutputEncoder encoder = encoders.get(node.getType());

		if (null != encoder) {
			encoder.encode(wrapper, node);
		}
	}

	/**
	 * Provide a means to associate json path to xpath expression when data is harvested from nodes by means
	 * other than extraction via the node's specified {@link Encoder}.
	 *
	 * @param wrapper parent {@link JsonWrapper}
	 * @param node decoded QRDA node
	 * @param leafLabel encoded json attribute name
	 */
	void maintainContinuity(JsonWrapper wrapper, Node node, String leafLabel) {
		Map<String, String> otherMeta = wrapper.createMetaMap(node, leafLabel);
		wrapper.mergeMetadata(otherMeta);
	}
}
