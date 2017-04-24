package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.ConversionEntry;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

import java.util.Collection;

/**
 * Top level Encoder for serializing into QPP format.
 */
public class ScopedQppOutputEncoder extends QppOutputEncoder{

	protected static final Registry<String, JsonOutputEncoder> ENCODERS = new Registry<>(Encoder.class);

	/**
	 * Encode the decoded node. If a {@link TemplateId#PLACEHOLDER} node is detected then assume
	 * the {@link ConversionEntry#scope} has been set to a level lower than {@link QrdaScope#CLINICAL_DOCUMENT}.
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
		if (node.getType() == TemplateId.PLACEHOLDER) {
			JsonWrapper scoped = new JsonWrapper();
			node.getChildNodes().stream().filter(this::inSpecifiedScope)
					.forEach(child -> {
				JsonWrapper childWrapper = new JsonWrapper();
				JsonOutputEncoder encoder = ENCODERS.get(child.getId());
				encoder.encode(childWrapper, child);
				scoped.putObject(childWrapper);
			});
			wrapper.putObject("scoped", scoped);
		} else {
			super.internalEncode(wrapper, node);
		}
	}

	/**
	 * Gatekeeper determining which decoded nodes may be encoded.
	 *
	 * @param node decoded node
	 * @return determination as to whether or not the given node may be encoded
	 */
	private boolean inSpecifiedScope(Node node) {
		String type = node.getType().name();
		Collection<QrdaScope> scope = ConversionEntry.getScope();
		return scope != null && scope.contains(QrdaScope.getInstanceByName(type));
	}

}
