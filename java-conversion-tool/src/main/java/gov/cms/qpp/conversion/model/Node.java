package gov.cms.qpp.conversion.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * Nodes can contain other nodes as children to create a hierarchy.
 */
public class Node {
	private final List<Node> childNodes = new ArrayList<>();
	private final Map<String, String> data = new HashMap<>();
	private final Map<String, List<String>> duplicateData = new HashMap<>();

	private TemplateId type;
	private Node parent;
	private boolean validated;

	private String defaultNsUri;
	private String path;

	/**
	 * Default constructor initializes internal list of Nodes
	 */
	public Node() {
		this(TemplateId.DEFAULT);
	}

	/**
	 * Constructor initialized with a Node and a template id String
	 *
	 * @param type of node
	 * @param parent this node's parent node
	 */
	public Node(TemplateId type, Node parent) {
		this(type);
		this.parent = parent;
	}

	/**
	 * Constructor initialized with a template id string
	 *
	 * @param templateId String of the parsed template id.
	 */
	public Node(TemplateId templateId) {
		this.type = templateId;
	}

	/**
	 * getValue returns the string value of the xml fragment parsed into this Node
	 *
	 * @param name String key for the value
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public <T> T getValue(String name) {
		return (T) data.get(name);
	}

	/**
	 * getDuplicateValues returns the string value of the xml fragment parsed into this Node
	 *
	 * @param name String key for the value
	 * @return mapped duplicates of target value
	 */
	@SuppressWarnings("unchecked")
	public <T> T getDuplicateValues(String name) {
		return (T) duplicateData.get(name);
	}

	/**
	 * putValue stores the Value under the key: name
	 *
	 * @param name  String key to store value under
	 * @param value String that is stored with this xml parsed Node
	 */
	public void putValue(String name, String value) {
		putValue(name, value, true);
	}

	/**
	 * putValue stores the Value under the key: name
	 *
	 * @param name  String key to store value under
	 * @param value String that is stored with this xml parsed Node
	 */
	public void putValue(String name, String value, boolean replace) {
		if (getValue(name) == null || replace) {
			data.put(name, value);
		} else {
			List<String> duplicates = Optional.ofNullable(duplicateData.get(name))
					.orElseGet(() -> {
				duplicateData.put(name, new ArrayList<>());
				return duplicateData.get(name);
			});
			duplicates.add(value);
		}
	}

	/**
	 * removeValue deletes the Value under the key: name
	 *
	 * @param name String key to remove value under
	 */
	public void removeValue(String name) {
		data.remove(name);
	}

	/**
	 * Check if a value has been assigned to the given key.
	 *
	 * @param name key
	 * @return corresponding value
	 */
	public boolean hasValue(String name) {
		return data.containsKey(name);
	}

	/**
	 * getChildNodes returns the list of child Nodes for this Node
	 *
	 * @return List of child Nodes.
	 */
	public List<Node> getChildNodes() {
		return childNodes;
	}

	/**
	 * Returns a list of child Nodes for this Node that satisfy the predicate.
	 *
	 * @param filter specifying match criteria
	 * @return List of matching child Nodes.
	 */
	public Stream<Node> getChildNodes(Predicate<Node> filter) {
		return childNodes.stream()
				.filter(filter);
	}

	/**
	 * Returns the first child Node from this Node that satisfies the predicate.
	 *
	 * @param filter specifying match criteria
	 * @return matching child Node.
	 */
	public Node findChildNode(Predicate<Node> filter) {
		return getChildNodes(filter).findFirst().orElse(null);
	}

	/**
	 * clears the old child nodes, replacing them with the given array
	 *
	 * @param childNodes vararg Node array
	 */
	public void setChildNodes(Node... childNodes) {
		this.childNodes.clear();
		this.childNodes.addAll(Arrays.asList(childNodes));
	}

	/**
	 * convenience for adding multiple child nodes
	 *
	 * @param childNodes vararg Node array
	 */
	public void addChildNodes(Node... childNodes) {
		this.childNodes.addAll(Arrays.asList(childNodes));
	}

	/**
	 * addChildNode will associate a Node to  this Node as a child
	 *
	 * @param childNode Node
	 */
	public void addChildNode(Node childNode) {
		if (childNode == null || childNode == this) {
			return;
		}
		this.childNodes.add(childNode);
	}

	/**
	 * toString will create a readable representation of this {@code Node}.
	 *
	 * @return A string representation
	 */
	@Override
	public String toString() {
		StringBuilder nodeToString = new StringBuilder("Node{");
		nodeToString.append("type=").append(type);
		nodeToString.append(", data=").append(data);
		nodeToString.append(", childNodes=").append("size:").append(childNodes.size());
		nodeToString.append(", parent=").append((parent == null) ? "null" : "not null");
		nodeToString.append(", validated=").append(validated);
		nodeToString.append(", defaultNsUri='").append(defaultNsUri).append('\'');
		nodeToString.append(", path='").append(path).append('\'');
		nodeToString.append('}');
		return nodeToString.toString();
	}

	/**
	 * getKeys gets the internal keyset for the list of Nodes
	 *
	 * @return The keys the value's set on this Node.
	 */
	public Set<String> getKeys() {
		return data.keySet();
	}

	/**
	 * getParent returns the Node associated to this Node as its parent
	 *
	 * @return Node
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * setParent associates a Node as this Node's parent
	 *
	 * @param parent Node
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * setType sets the TemplateId backing the node
	 *
	 * @param type TemplateId
	 */
	public void setType(TemplateId type) {
		this.type = type;
	}

	/**
	 * getTemplateId returns the Internal TemplateId associated to this Node
	 *
	 * @return TemplateId
	 */
	public TemplateId getType() {
		return type;
	}

	/**
	 * Returns the path from the original document this {@code Node} is associated with.
	 *
	 * @return The path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path from the original document that this {@code Node} is associated with.
	 *
	 * @param newPath The path.
	 */
	public void setPath(String newPath) {
		path = newPath;
	}

	/**
	 * Returns the defaultNsUri from the original document this {@code Node} is associated with.
	 *
	 * @return The default namespace URI.
	 */
	public String getDefaultNsUri() {
		return defaultNsUri;
	}

	/**
	 * Sets the defaultNsUri from the original document that this {@code Node} is associated with.
	 *
	 * @param newDefaultNsUri updated default namespace URI.
	 */
	public void setDefaultNsUri(String newDefaultNsUri) {
		defaultNsUri = newDefaultNsUri;
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param templateId templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	public List<Node> findNode(TemplateId templateId) {
		return findNode(templateId, null);
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param templateId templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @param bail lambda that consumes a list and returns a boolean that governs early exit
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	private List<Node> findNode(TemplateId templateId, Predicate<List<?>> bail) {
		List<Node> foundNodes = new ArrayList<>();
		if (this.type == templateId) {
			foundNodes.add(this);
		}
		for (Node childNode : childNodes) {
			if (bail != null && bail.test(foundNodes)) {
				break;
			}
			List<Node> matches = childNode.findNode(templateId, bail);
			foundNodes.addAll(matches);
		}
		return foundNodes;
	}

	/**
	 * Search this and child nodes for first node with matching id
	 *
	 * @param templateId TemplateId that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return the first {@link gov.cms.qpp.conversion.model.Node} in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id or null
	 * if no matches are found
	 */
	public Node findFirstNode(TemplateId templateId) {
		List<Node> nodes = this.findNode(templateId, Node::foundNode);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	/**
	 * setValidated sets the internal state of this Node validation.
	 * Used to control the recursion of nested validations
	 *
	 * @param validated boolean
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * isValidated returns the state of this Node validation
	 *
	 * @return boolean
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * foundNode checks to see if any Node exists in the List
	 *
	 * @param nodes A list
	 * @return Whether the list has an element or not
	 */
	private static boolean foundNode(List<?> nodes) {
		return !nodes.isEmpty();
	}
}
