package gov.cms.qpp.conversion.encode;

import java.util.List;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Node;

@Encoder(templateId = "2.16.840.1.113883.10.20.27.1.2")
public class ClinicalDocumentEncoder extends QppOutputEncoder {

	public ClinicalDocumentEncoder() {
	}

	@Override
	public void internalEcode(JsonWrapper wrapper, Node node) throws EncodeException {

		wrapper.putString("programName", node.getValue("programName"));
		wrapper.putString("entityType", "individual");
		wrapper.putString("taxpayerIdentificationNumber", node.getValue("taxpayerIdentificationNumber"));
		wrapper.putString("nationalProviderIdentifier", node.getValue("nationalProviderIdentifier"));

		String performanceStart = node.getValue("performanceStart");
		String performanceEnd = node.getValue("performanceEnd");
		String performanceYear = performanceStart.substring(0, 4);

		wrapper.putInteger("performanceYear", performanceYear);

		List<Node> children = node.getChildNodes();

		JsonWrapper measurementSetsWrapper = new JsonWrapper();

		JsonWrapper childWrapper = new JsonWrapper();
		for (Node child : children) {
			JsonOutputEncoder sectionEncoder = encoders.get(child.getId());

			if (null != sectionEncoder) { // currently don't have a set of IA
											// Encoders, but this will protect
											// against others
				sectionEncoder.encode(childWrapper, child);

				childWrapper.putString("source", "provider");
				childWrapper.putString("performanceStart", performanceStart);
				childWrapper.putString("performanceEnd", performanceEnd);

				measurementSetsWrapper.putObject(childWrapper.getObject());
			}
		}
		wrapper.putObject("measurementSets", measurementSetsWrapper.getObject());

	}

}
