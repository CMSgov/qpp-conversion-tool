package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

public class NodeHelper {
	private NodeHelper() {
		//private and empty because this is a utility class
	}

	/**
	 * Find a specific parent node of current node given
	 *
	 * @param node
	 * @param templateId
	 * @return
	 */
	public static Node findParent(Node node, TemplateId templateId) {
		Node currentParent = node.getParent();
		Node currentNode = new Node(templateId);
		if (currentParent != null && templateId.getRoot().equalsIgnoreCase(currentParent.getType().getRoot())) {
			currentNode = currentParent;
		} else if ( currentParent != null) {
			currentNode = findParent(currentParent, templateId);
		}
		return currentNode;
	}
}
