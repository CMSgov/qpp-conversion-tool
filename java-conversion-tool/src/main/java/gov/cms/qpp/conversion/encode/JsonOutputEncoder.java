package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
	private List<Detail> details = new ArrayList<>();

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
			details.add(new Detail("Failure to encode"));
		}
	}

	public void encode(JsonWrapper wrapper, Node node) {
		try {
			internalEncode(wrapper, node);
			if (wrapper.isObject()) {
				wrapper.attachMetadata(node);
			}
		} catch (EncodeException e) {
			DEV_LOG.warn("Encode error when doing internalEncode, adding a new Detail", e);
			details.add(new Detail(e.getMessage()));
		}
	}

	@Override
	public JsonWrapper encode() {
		JsonWrapper wrapper = new JsonWrapper();
		for (Node curNode : nodes) {
			encode(wrapper, curNode);
		}
		return wrapper;
	}

	public void addValidationError(Detail detail) {
		details.add(detail);
	}

	public List<Detail> getDetails() {
		return this.details;
	}

	public void setNodes(List<Node> someNodes) {
		this.nodes = someNodes;
	}

	protected abstract void internalEncode(JsonWrapper wrapper, Node node);
}
