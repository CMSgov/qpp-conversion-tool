package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Top level Encoder for serializing into QPP format.
 */
public class ScopedQppOutputEncoder extends QppOutputEncoder {

	public ScopedQppOutputEncoder(Context context) {
		super(context);
	}

	/**
	 * Encode the decoded node.
	 *
	 * @param wrapper object to encode into
	 * @param node object to encode
	 * @throws EncodeException If error occurs during encoding
	 */
	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		if (node.getType() == TemplateId.PLACEHOLDER) {
			JsonWrapper scoped = new JsonWrapper();
			node.getChildNodes().stream()
					.flatMap(this::flattenNode)
					.filter(this::inSpecifiedScope)
					.forEach(child -> {
				JsonWrapper childWrapper = new JsonWrapper();
				JsonOutputEncoder encoder = encoders.get(child.getType());
				encoder.encode(childWrapper, child);
				scoped.putObject(childWrapper);
			});
			wrapper.putObject("scoped", scoped);
		} else {
			super.internalEncode(wrapper, node);
		}
	}

	/**
	 * Recurse node hierarchy and flatten into stream.
	 *
	 * @param node top of hierarchy
	 * @return flattened hierarchy
	 */
	private Stream<Node> flattenNode(Node node) {
		return Stream.concat(Stream.of(node),
				node.getChildNodes().stream().flatMap(this::flattenNode));
	}

	/**
	 * Gatekeeper determining which decoded nodes may be encoded.
	 *
	 * @param node decoded node
	 * @return determination as to whether or not the given node may be encoded
	 */
	private boolean inSpecifiedScope(Node node) {
		String type = node.getType().name();
		Collection<QrdaScope> scope = context.getScope();
		return scope.contains(QrdaScope.getInstanceByName(type));
	}

}
