package gov.cms.qpp.conversion.model;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {

	PLACEHOLDER             ("placeholder"),
	MEASURE_SECTION		    ("2.16.840.1.113883.10.20.24.2.2"),
	CLINICAL_DOCUMENT       ("2.16.840.1.113883.10.20.27.1.2"),
	MEASURE_SECTION1	    ("2.16.840.1.113883.10.20.27.2.3","2016-11-01"),
	IA_SECTION              ("2.16.840.1.113883.10.20.27.2.4"),
	ACI_SECTION             ("2.16.840.1.113883.10.20.27.2.5"),
	REPORTING_NODE          ("2.16.840.1.113883.10.20.27.2.6","2016-11-01"),
	ACI_AGGREGATE_COUNT     ("2.16.840.1.113883.10.20.27.3.3"),
	IPP_POPULATION		    ("2.16.840.1.113883.10.20.27.3.16","2016-11-01"),
	MEASURE_ENTRY_NQF	    ("2.16.840.1.113883.10.20.27.3.17","2016-11-01"),
	PAYER_SUPPLEMENT	    ("2.16.840.1.113883.10.20.27.3.18","2016-11-01"),
	RACE_SUPPLEMENT		    ("2.16.840.1.113883.10.20.27.3.19","2016-11-01"),
	UNKNOWN			        ("2.16.840.1.113883.10.20.27.3.20"),
	GENDER_MALE		        ("2.16.840.1.113883.10.20.27.3.21","2016-11-01"),
	ETHNICITY_SUPPLEMENT	("2.16.840.1.113883.10.20.27.3.22","2016-11-01"),
	REPORTING_NODE_DRIV	    ("2.16.840.1.113883.10.20.27.3.23","2016-11-01"),
	IPP_POPULATION_1	    ("2.16.840.1.113883.10.20.27.3.24"),
	PERFORMANCE_RATE_1	    ("2.16.840.1.113883.10.20.27.3.25","2016-11-01"),
	UNKNOWN_1		        ("2.16.840.1.113883.10.20.27.3.26"),
	ACI_PROPORTION          ("2.16.840.1.113883.10.20.27.3.28"),
	UNKNOWN_2   		    ("2.16.840.1.113883.10.20.27.3.29"),
	PERFORMANCE_RATE        ("2.16.840.1.113883.10.20.27.3.30"),
	ACI_NUMERATOR           ("2.16.840.1.113883.10.20.27.3.31"),
	ACI_DENOMINATOR         ("2.16.840.1.113883.10.20.27.3.32"),
	IA_MEASURE              ("2.16.840.1.113883.10.20.27.3.33"),
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
