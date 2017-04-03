package gov.cms.qpp.conversion.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * 
 * Nodes can contain other nodes as children to create a hierarchy.
 * @author David Uselmann
 * 
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 4602134063479322076L;

	private String internalId;
	private NodeType type;
	private Map<String, String> data = new HashMap<>();

	private List<Node> childNodes;

	private Node parent;

	private boolean validated;

	public Node() {
		this.data = new HashMap<>();
		this.setChildNodes(new ArrayList<>());
	}

	public Node(String id) {
		this();
		setId(id);
		this.type = NodeType.getTypeById(id);
	}

	public Node(Node parentNode) {
		this();
		this.parent = parentNode;
	}

	public Node(Node parentNode, String id) {
		this(parentNode);
		setId(id);
		this.type = NodeType.getTypeById(id);
	}

	public String getValue(String name) {
		return data.get(name);
	}

	public void putValue(String name, String value) {
		data.put(name, value);
	}

	public void setId(String templateId) {
		this.internalId = templateId;
	}

	public String getId() {
		return internalId;
	}

	public List<Node> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<Node> childNodes) {
		this.childNodes = childNodes;
	}

	public void addChildNode(Node childNode) {
		if (childNode == null || childNode == this) {
			return;
		}
		this.childNodes.add(childNode);
	}

	@Override
	public String toString() {
		return toString("");// no tabs to start
	}

	protected String toString(String tabs) {
		return tabs + selfToString() + "\n" + childrenToString(tabs + "\t");
	}

	protected String selfToString() {
		return "Node: internalId: " + internalId + ", data: " + data;
	}

	protected String childrenToString(String tabs) {
		StringBuilder children = new StringBuilder();
		if (childNodes.isEmpty()) {
			children.append(" -> (none)");
		} else {
			children.append(": \n");
			String sep = "";
			String toBeSep = "\n";
			for (Node child : childNodes) {
				children.append(sep).append(child.toString(tabs));
				sep = toBeSep;
			}
		}
		return tabs + "childNodes of " + internalId + children;
	}

	public Set<String> getKeys() {
		return data.keySet();
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public Node findFirstNode(String id) {
		List<Node> nodes = this.findNode(id);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	public List<Node> findNode(String id) {
		List<Node> foundNodes = new ArrayList<>();

		if (id.equals(this.internalId)) {
			foundNodes.add(this);
		}

		for (Node childNode : childNodes) {
			foundNodes.addAll(childNode.findNode(id));
		}

		return foundNodes;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public NodeType getType() {
		return type;
	}

}
