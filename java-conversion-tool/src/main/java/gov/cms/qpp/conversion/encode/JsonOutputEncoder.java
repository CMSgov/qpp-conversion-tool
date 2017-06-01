package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Output JSON to a Writer.
 *
 * @author Scott Fradkin
 *
 */
public abstract class JsonOutputEncoder implements OutputEncoder {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(JsonOutputEncoder.class);
	private List<Node> nodes;
	private List<ValidationError> validationErrors = new ArrayList<>();

	@Override
	public void encode(Writer writer) {
		try {
			JsonWrapper wrapper = new JsonWrapper();
			for (Node curNode : nodes) {
				encode(wrapper, curNode);
			}
			writer.write(wrapper.toString());
			writer.flush();
		} catch (IOException exception) {
			DEV_LOG.error("Couldn't write out JSON file.", exception);
			validationErrors.add(new ValidationError("Failure to encode"));
		}
	}

	public final void encode(JsonWrapper wrapper, Node node) {
		try {
			internalEncode(wrapper, node);
			if (wrapper.isObject()) {
				wrapper.attachMetadata(node);
			}
		} catch (EncodeException e) {
			DEV_LOG.warn("Encode error when doing internalEncode, adding a new ValidationError", e);
			validationErrors.add(new ValidationError(e.getMessage()));
		}
	}

	@Override
	public InputStream encode() {
		JsonWrapper wrapper = new JsonWrapper();
		for (Node curNode : nodes) {
			encode(wrapper, curNode);
		}
		return new ByteArrayInputStream(wrapper.toString().getBytes());
	}

	public void addValidationError(ValidationError validationError) {
		validationErrors.add(validationError);
	}

	public List<ValidationError> getValidationErrors() {
		return this.validationErrors;
	}

	public void setNodes(List<Node> someNodes) {
		this.nodes = someNodes;
	}

	protected abstract void internalEncode(JsonWrapper wrapper, Node node);
}
