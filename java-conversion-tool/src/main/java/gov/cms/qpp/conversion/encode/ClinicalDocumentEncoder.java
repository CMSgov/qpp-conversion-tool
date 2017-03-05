package gov.cms.qpp.conversion.encode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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


		List<Node> children = node.getChildNodes();
		
		Map<String, Node> childMapByTemplateId = children.stream().collect(
				Collectors.toMap(Node::getId,Function.identity(), (v1,v2)->v1, LinkedHashMap::new));
		
		Node reportingParametersNode = childMapByTemplateId.remove("2.16.840.1.113883.10.20.27.2.6");
		
		String performanceStart = null;
		String performanceEnd = null;
		
		if (null != reportingParametersNode && null != reportingParametersNode.getChildNodes() && !reportingParametersNode.getChildNodes().isEmpty()) {
			
			Node act = reportingParametersNode.getChildNodes().get(0);
			
			performanceStart = act.getValue("performanceStart");
			performanceEnd = act.getValue("performanceEnd");
			
			if (null != performanceStart) {
				String performanceYear = performanceStart.substring(0, 4);
				wrapper.putInteger("performanceYear", performanceYear);
			}

		}
		
		JsonWrapper measurementSetsWrapper = new JsonWrapper();


		JsonWrapper childWrapper;
		for (Node child : childMapByTemplateId.values()) {
			childWrapper = new JsonWrapper();
			JsonOutputEncoder sectionEncoder = encoders.get(child.getId());

			if (null != sectionEncoder) { // currently don't have a set of IA
											// Encoders, but this will protect
											// against others
				
				sectionEncoder.encode(childWrapper, child);

				childWrapper.putString("source", "provider");
				childWrapper.putDate("performanceStart", performanceStart);
				childWrapper.putDate("performanceEnd", performanceEnd);

				measurementSetsWrapper.putObject(childWrapper.getObject());
			}
		}
		wrapper.putObject("measurementSets", measurementSetsWrapper.getObject());

	}

}
