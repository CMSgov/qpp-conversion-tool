package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Validatable;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Output JSON to a Writer.
 *
 * @author Scott Fradkin
 *
 */
public abstract class JsonOutputEncoder implements OutputEncoder, Validatable<String, String> {

    List<Node> nodes;

    public JsonOutputEncoder() {
    }

    @Override
    public void encode(Writer writer) throws EncodeException {

        Validations.init();

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
        return Validations.values();
    }

    @Override
    public List<String> getValidationsById(String templateId) {
        return Validations.getValidationsById(templateId);
    }

    @Override
    public void addValidation(String templateId, String validation) {
        Validations.addValidation(templateId, validation);
    }

    public void addValidation(String templateId, EncodeException e) {
        Validations.addValidation(templateId, e.getMessage());
    }

    public void setNodes(List<Node> someNodes) {
        this.nodes = someNodes;
    }

    public final void encode(JsonWrapper wrapper, Node node) {
        try {
            internalEncode(wrapper, node);
        } catch (EncodeException e) {
            Validations.addValidation(e.getTemplateId(), e.getMessage());
        }
    }

    protected abstract void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException;
}
