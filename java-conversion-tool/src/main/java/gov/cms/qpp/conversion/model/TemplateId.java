package gov.cms.qpp.conversion.model;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {

	PLACEHOLDER             ("placeholder"),
	CLINICAL_DOCUMENT       ("2.16.840.1.113883.10.20.27.1.2"),
	ACI_AGGREGATE_COUNT     ("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION              ("2.16.840.1.113883.10.20.27.2.4"),
	ACI_SECTION             ("2.16.840.1.113883.10.20.27.2.5"),
	ACI_PROPORTION          ("2.16.840.1.113883.10.20.27.3.28"),
	ACI_NUMERATOR           ("2.16.840.1.113883.10.20.27.3.31"),
	ACI_DENOMINATOR         ("2.16.840.1.113883.10.20.27.3.32"),
	IA_MEASURE              ("2.16.840.1.113883.10.20.27.3.33"),
	DEFAULT                 ("default");

	private final String root;
	private final String extension;

	TemplateId(final String root) {
		this.root = root;
		extension = "";
	}

	TemplateId(final String root, final String extension) {
		this.root = root;
		this.extension = extension;
	}

	public String getRoot() {
		return root;
	}

	public String getExtension() {
		return extension;
	}

	public String getTemplateId() {
		String templateId = getRoot();
		String extension = getExtension();

		if (!extension.isEmpty()) {
			templateId += templateId + ":" + extension;
		}
		return templateId;
	}

	public static TemplateId getTypeById(String id) {
		for (TemplateId type : TemplateId.values()) {
			if (type.getTemplateId().equals(id)) {
				return type;
			}
		}

		return TemplateId.DEFAULT;
	}

}
