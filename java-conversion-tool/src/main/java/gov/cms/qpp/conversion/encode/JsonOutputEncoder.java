package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import gov.cms.qpp.conversion.Validatable;
import gov.cms.qpp.conversion.Validations;
import gov.cms.qpp.conversion.model.Node;

/**
 * Output JSON to a Writer
 * 
 */
public abstract class JsonOutputEncoder implements OutputEncoder, Validatable<String, String> {

	// keep it ordered since we can only 
	// use this storage method on a single threaded app anyway
	protected static ThreadLocal<Validations<String, String>> validations = new ThreadLocal<>();

	List<Node> nodes;

	public JsonOutputEncoder() {
	}

	@Override
	public void encode(Writer writer) throws EncodeException {
		validations.set(new Validations<>());
		
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

	@Override
	public Iterable<String> validations() {
		return validations.get().validations();
	}

	@Override
	public List<String> getValidationsById(String templateId) {
		return validations.get().getValidationsById(templateId);
	}
	
	@Override
	public void addValidation(String templateId, String validation) {
		validations.get().addValidation(templateId, validation);
	}
	
	public void addValidation(String templateId, EncodeException e) {
		validations.get().addValidation(templateId, e.getMessage());
	}
	
	
	public void setNodes(List<Node> someNodes) {
		this.nodes = someNodes;
	}

	
	public final void encode(JsonWrapper wrapper, Node node) {
		try {
			internalEcode(wrapper, node);
		} catch (EncodeException e) {
			validations.get().addValidation(e.getTemplateId(), e.getMessage());
		}
	}
	
	protected abstract void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException;
}
