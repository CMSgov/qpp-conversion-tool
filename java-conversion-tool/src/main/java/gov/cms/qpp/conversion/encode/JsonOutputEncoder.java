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
			writer.write("{\n");

			for (Node curNode : nodes) {
				encode(writer, curNode, 1);
			}

			writer.write("\n}");

			writer.flush();

		} catch (IOException e) {
			throw new EncodeException("Failure to encode", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				throw new EncodeException("Failure to close writer", e);
			}
		}

	}

	public void setNodes(List<Node> someNodes) {
		this.nodes = someNodes;
	}

	protected void writeIndents(Writer writer, int indentLevel) throws EncodeException {
		if (indentLevel == 0) {
			return;
		}

		for (int i = indentLevel; i > 0; i--) {
			try {
				writer.write("\t");
			} catch (IOException e) {
				throw new EncodeException("Failure to write indents", e);
			}
		}
	}

	public abstract void encode(Writer writer, Node node, int indentLevel) throws EncodeException;

}
