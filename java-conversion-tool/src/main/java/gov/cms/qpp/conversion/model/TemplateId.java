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
	MEASURE_PERFORMED 		("2.16.840.1.113883.10.20.27.3.27"),
	ACI_PROPORTION          ("2.16.840.1.113883.10.20.27.3.28"),
	ACI_NUMERATOR           ("2.16.840.1.113883.10.20.27.3.31"),
	ACI_DENOMINATOR         ("2.16.840.1.113883.10.20.27.3.32"),
	IA_MEASURE              ("2.16.840.1.113883.10.20.27.3.33"),
	NATIONAL_PROVIDER 		("2.16.840.1.113883.4.6"),
	REPRESENTED_ORG			("2.16.840.1.113883.4.2"),
	STRUCTURED_BODY			("2.16.840.1.113883.10.20.27.2.5"),
	DEFAULT                 ("default");

	private final String root;
	private final String extension;

	/**
	 * Constructs a TemplateId with just a root.
	 *
	 * @param root The root of the template ID.  Normally numbers with decimal points in between.
	 */
	TemplateId(final String root) {
		this.root = root;
		extension = "";
	}

	/**
	 * Constructs a TemplateId with a root and an extension.
	 *
	 * @param root The root of the template ID.  Normally numbers with decimal points in between.
	 * @param extension The extension of the template ID.  Normally a date.
	 */
	TemplateId(final String root, final String extension) {
		this.root = root;
		this.extension = extension;
	}

	/**
	 * @return The root of the template ID.
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @return The extension of the template ID.
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return The complete template ID which includes a concatenation of the root followed by a colon followed by the
	 * extension.
	 */
	public String getTemplateId() {
		String templateId = getRoot();
		String extension = getExtension();

		if (!extension.isEmpty()) {
			templateId += ":" + extension;
		}
		return templateId;
	}

	/**
	 * Returns the enumeration for the specified ID.
	 *
	 * @param id The complete template ID (root + ":" + extension).
	 * @return The template ID if found.  Else {@code TemplateId.DEFAULT}.
	 */
	public static TemplateId getTypeById(String id) {
		for (TemplateId type : TemplateId.values()) {
			if (type.getTemplateId().equals(id)) {
				return type;
			}
		}

		return TemplateId.DEFAULT;
	}
}
