package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.model.Node;

/**
 * Output JSON to a Writer
 * 
 */
public abstract class JsonOutputEncoder implements OutputEncoder {

	List<Node> nodes;

	public JsonOutputEncoder() {
	}

	@Override
	public void encode(Writer writer) throws EncodeException {
		try {
			JsonWrapper wrapper = new JsonWrapper();
			for (Node curNode : nodes) {
				encode(wrapper, curNode);
			}
			writer.write(wrapper.toString());
			writer.flush();
		} catch (IOException e) {
			throw new EncodeException("Failure to encode", e);
		}

	}

	public void setNodes(List<Node> someNodes) {
		this.nodes = someNodes;
	}

	public abstract void encode(JsonWrapper wrapper, Node node);
}
