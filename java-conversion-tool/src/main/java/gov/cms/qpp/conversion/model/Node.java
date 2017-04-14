package gov.cms.qpp.conversion.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * <p>
 * Nodes can contain other nodes as children to create a hierarchy.
 *
 * @author David Uselmann
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 4602134063479322076L;
	private TemplateId type;
	private Map<String, String> data = new HashMap<>();

	private List<Node> childNodes;
	private Node parent;
	private boolean validated;
	private String internalId;

	/**
	 * Default constructor initializes internal list of Nodes
	 */
	public Node() {
		this.childNodes = new ArrayList<>();
	}

	/**
	 * Constructor initialized with a template id string
	 *
	 * @param id String of the parsed template id.
	 */
	public Node(String id) {
		this();
		setId(id);
		this.type = TemplateId.getTypeById(id);
	}

	/**
	 * Constructor initialized with a Node
	 *
	 * @param parentNode Node
	 */
	public Node(Node parentNode) {
		this();
		this.parent = parentNode;
	}

	/**
	 * Constructor initialized with a Node and a template id String
	 *
	 * @param parentNode Node
	 * @param id         String representation of a template id
	 */
	public Node(Node parentNode, String id) {
		this(parentNode);
		setId(id);
	}

	/**
	 * getValue returns the string value of the xml fragment parsed into this Node
	 *
	 * @param name String key for the value
	 * @return String
	 */
	public String getValue(String name) {
		return data.get(name);
	}

	/**
	 * putValue stores the Value under the key: name
	 *
	 * @param name  String key to store value under
	 * @param value String that is stored with this xml parsed Node
	 */
	public void putValue(String name, String value) {
		data.put(name, value);
	}

	/**
	 * setId locates the appropriate TemplateId and sets the Node Type
	 *
	 * @param templateId String from the parsed xml fragment
	 */
	public void setId(String templateId) {
		this.type = TemplateId.getTypeById(templateId);
		this.internalId = templateId;
	}

	/**
	 * getId returns the internal template id string
	 *
	 * @return String
	 */
	public String getId() {
		return internalId;
	}

	/**
	 * getChildNodes returns the list of child Nodes for this Node
	 *
	 * @return List<Node>
	 */
	public List<Node> getChildNodes() {
		return childNodes;
	}

	/**
	 * convenience for adding multiple child nodes
	 *
	 * @param childNodes vararg Node array
	 */
	public void addChildNodes(Node... childNodes) {
		this.setChildNodes(Arrays.asList(childNodes));
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
	 * toString will create a readable representation of this Node
	 * Node: templateId: PERFORMANCE_RATE, data: {DefaultDecoderFor=Performance Rate}
	 * childNodes of PERFORMANCE_RATE -> (none)
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		return toString("");// no tabs to start
	}

	/**
	 * getKeys gets the internal keyset for the list of Nodes
	 *
	 * @return Set<String>
	 */
	public Set<String> getKeys() {
		return data.keySet();
	}



	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	public List<Node> findNode(String id) {
		return findNode(id, null);
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
	 * getTemplateId returns the Internal TemplateId associated to this Node
	 *
	 * @return TemplateId
	 */
	public TemplateId getType() {
		return type;
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param id   templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @param bail lambda that consumes a list and returns a boolean that governs early exit
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	private List<Node> findNode(String id, Predicate<List<?>> bail) {
		List<Node> foundNodes = new ArrayList<>();
		if (id.equals(getId())) {
			foundNodes.add(this);
		}
		for (Node childNode : childNodes) {
			if (bail != null && bail.test(foundNodes)) {
				break;
			}
			List<Node> matches = childNode.findNode(id, bail);
			foundNodes.addAll(matches);
		}
		return foundNodes;
	}
	/**
	 * Search this and child nodes for first node with matching id
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return the first {@link gov.cms.qpp.conversion.model.Node} in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id or null
	 * if no matches are found
	 */
	protected Node findFirstNode(String id) {
		List<Node> nodes = this.findNode(id, Node::foundNode);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	/**
	 * protected toString builds the string representation of this Node
	 *
	 * @param tabs String used for indentation of multiline strings
	 * @return String
	 */
	protected String toString(String tabs) {
		return tabs + selfToString() + "\n" + childrenToString(tabs + "\t");
	}

	/**
	 * setValidated sets the internal state of this Node validation.
	 * Used to control the recursion of nested validations
	 *
	 * @param validated boolean
	 */
	protected void setValidated(boolean validated) {
		this.validated = validated;
	}
	/**
	 * isValidated returns the internal state of this Node validation
	 *
	 * @return boolean
	 */
	protected boolean isValidated() {
		return validated;
	}

	/**
	 * setChildNodes will associate nested xml components with this parsed xml
	 * fragment Node
	 *
	 * @param childNodes List<Node>
	 */
	private void setChildNodes(List<Node> childNodes) {
		this.childNodes = childNodes;
	}

	/**
	 * selfToString helps build up the string representation of this node
	 *
	 * @return String
	 */
	private String selfToString() {
		return "Node: templateId: " + getTypeName(type) + ", data: " + data;
	}

	/**
	 * childrenToString recurse through the Child Nodes of this Node and
	 * build the string representation of each.
	 *
	 * @param tabs String
	 * @return String
	 */
	private String childrenToString(String tabs) {
		StringJoiner children = new StringJoiner("\n");
		if (childNodes.isEmpty()) {
			children.add(" -> (none)");
		} else {
			children.add(": ");
			for (Node child : childNodes) {
				children.add(child.toString(tabs));
			}
		}
		return tabs + "childNodes of " + getTypeName(type) + children;
	}

	/**
	 * foundNode checks to see if any Node exists in the List
	 *
	 * @param nodes List<?> nodes
	 * @return Boolean
	 */
	private static Boolean foundNode(List<?> nodes) {
		return !nodes.isEmpty();
	}

	/**
	 * getTypeName will return the String representation of the TemplateId enum
	 * if available useful for toString representations of this Node.
	 *
	 * @param type TemplateId
	 * @return String
	 */
	private String getTypeName(TemplateId type) {
		return ("DEFAULT".equals(type.name()) ||
				"PLACEHOLDER".equals(type.name()) ? getId() : type.name());
	}
}
