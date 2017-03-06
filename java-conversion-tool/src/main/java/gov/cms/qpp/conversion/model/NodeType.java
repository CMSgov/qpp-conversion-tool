package gov.cms.qpp.conversion.model;

public enum NodeType {

	CLINICAL_DOCUMENT("2.16.840.1.113883.10.20.27.1.2"),
	ACI_SECTION("2.16.840.1.113883.10.20.27.2.5"),
	IA_SECTION("2.16.840.1.113883.10.20.27.2.4"),
	IA_MEASURE("2.16.840.1.113883.10.20.27.3.33"),
	ACI_MEASURE("2.16.840.1.113883.10.20.27.3.28"),
	ACI_NUMERATOR("2.16.840.1.113883.10.20.27.3.31"),
	ACI_DENOMINATOR("2.16.840.1.113883.10.20.27.3.32"),
	ACI_NUM_DENOM_VALUE("2.16.840.1.113883.10.20.27.3.3"),
	PLACEHOLDER("placeholder"),
	DEFAULT("default");

	private final String templateId;

	NodeType(String id) {
		this.templateId = id;
	}

	public String getTemplateId() {
		return this.templateId;
	}

	public static NodeType getTypeById(String id) {
		for (NodeType type : NodeType.values()) {
			if (type.getTemplateId().equals(id)) {
				return type;
			}
		}

		// return node type of DEFAULT if nothing is found
		// there are a set of default encoders that will create placeholder
		// nodes for nodes that may
		// have real encoders in the future
		return NodeType.DEFAULT;
	}

}
