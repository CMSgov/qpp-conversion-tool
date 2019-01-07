package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Output JSON to a Writer.
 */
public abstract class JsonOutputEncoder implements OutputEncoder {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(JsonOutputEncoder.class);
	private List<Node> nodes;
	private List<Detail> errors = new ArrayList<>();
	private List<Detail> warnings = new ArrayList<>();

	@Override
	public void encode(Writer writer) {
		JsonWrapper wrapper = new JsonWrapper();
		for (Node curNode : nodes) {
			encode(wrapper, curNode);
		}
		try {
			writer.write(wrapper.toString());
			writer.flush();
		} catch (IOException exception) {
			DEV_LOG.error("Couldn't write out JSON file.", exception);
			Detail detail = Detail.forErrorCode(ErrorCode.UNEXPECTED_ENCODE_ERROR);
			detail.setMessage(exception.getMessage());
			errors.add(detail);
		}
	}

	/**
	 * Encode given node into json
	 *
	 * @param wrapper structure that facilitates json serialization
	 * @param node structure to be converted to json
	 */
	public void encode(JsonWrapper wrapper, Node node) {
		encode(wrapper, node, true);
	}

	/**
	 * Encode given node into json. Optionally include metadata about originating node.
	 *
	 * @param wrapper structure that facilitates json serialization
	 * @param node structure to be converted to json
	 * @param mergeMetadata instruction on whether or not metadata should be included in the wrapper
	 */
	public void encode(JsonWrapper wrapper, Node node, boolean mergeMetadata) {
		try {
			internalEncode(wrapper, node);
			if (mergeMetadata && wrapper.isObject()) {
				wrapper.attachMetadata(node);
			}
		} catch (EncodeException exception) {
			DEV_LOG.warn("Encode error when doing internalEncode, adding a new Detail", exception);
			Detail detail = Detail.forErrorAndNode(ErrorCode.UNEXPECTED_ENCODE_ERROR, node);
			detail.setMessage(exception.getMessage());
			addValidationError(detail);
		}
	}

	/**
	 * Encodes the nodes as JSON.
	 * @return a custom JSON wrapper class that knows how to process QPP Nodes.
	 */
	@Override
	public JsonWrapper encode() {
		JsonWrapper wrapper = new JsonWrapper();
		for (Node curNode : nodes) {
			encode(wrapper, curNode);
		}
		return wrapper;
	}

	/**
	 * Add a new validation error
	 * @param detail the error information
	 */
	public void addValidationError(Detail detail) {
		errors.add(detail);
	}

	/**
	 * Add a new validation warning
	 * @param detail the warning information
	 */
	public void addValidationWarning(Detail detail) {
		warnings.add(detail);
	}

	/**
	 * get the list of all validation errors.
	 * 
	 * @return list of error details
	 */
	public List<Detail> getErrors() {
		return this.errors;
	}

	/**
	 * get the list of all validation earnings.
	 * 
	 * @return list of warning details
	 */
	public List<Detail> getWarnings() {
		return this.warnings;
	}

	/**
	 * Assign a new list of QPP element nodes.
	 * @param someNodes the new list of nodes
	 */
	public void setNodes(List<Node> someNodes) {
		this.nodes = someNodes;
	}

	/**
	 * Subclasses must implement this method with the
	 * specific encoding method for its node type handling.
	 * 
	 * @param wrapper the entire JSON node collection.
	 * @param node the current node
	 */
	protected abstract void internalEncode(JsonWrapper wrapper, Node node);
}
