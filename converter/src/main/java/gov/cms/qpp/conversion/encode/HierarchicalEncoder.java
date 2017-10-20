package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;

public class HierarchicalEncoder extends JsonOutputEncoder{

	@Override
	protected void internalEncode(JsonWrapper wrapper, Node node) {
		wrapper.putString("template", abbreviate(node));
		if (node.getChildNodes().size() > 0) {
			JsonWrapper children = new JsonWrapper();
			node.getChildNodes().forEach(child -> {
				JsonWrapper childWrapper = new JsonWrapper();
				internalEncode(childWrapper, child);
				children.putObject(childWrapper);
			});

			wrapper.putObject("children", children);
		}
	}

	private String abbreviate(Node node) {
		return node.getType().name() + " (" + node.getType().getRoot() + ")";
	}
}
