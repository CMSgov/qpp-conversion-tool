package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.2.5")
public class AciSectionEncoder extends QppOutputEncoder {

	public AciSectionEncoder() {
	}

	@Override
	public void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {

		wrapper.putString("category", node.getValue("category"));

		List<Node> children = node.getChildNodes();

		JsonWrapper measurementsWrapper = new JsonWrapper();

		JsonWrapper childWrapper = new JsonWrapper();
		for (Node child : children) {
			JsonOutputEncoder measureEncoder = encoders.get(child.getId());
			measureEncoder.encode(childWrapper, child);
			measurementsWrapper.putObject(childWrapper.getObject());
		}
		wrapper.putObject("measurements", measurementsWrapper.getObject());

	}

}
