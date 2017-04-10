package gov.cms.qpp.conversion.model;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {

	PLACEHOLDER                  ("placeholder"),
	CLINICAL_DOCUMENT            ("2.16.840.1.113883.10.20.27.1.2"),
	ACI_AGGREGATE_COUNT          ("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION                   ("2.16.840.1.113883.10.20.27.2.4"),
	ACI_SECTION                  ("2.16.840.1.113883.10.20.27.2.5"),
	ACI_MEASURE_PERFORMED        ("2.16.840.1.113883.10.20.27.3.27"),
	ACI_PROPORTION               ("2.16.840.1.113883.10.20.27.3.28"),
	ACI_NUMERATOR                ("2.16.840.1.113883.10.20.27.3.31"),
	ACI_DENOMINATOR              ("2.16.840.1.113883.10.20.27.3.32"),
	IA_MEASURE                   ("2.16.840.1.113883.10.20.27.3.33"),
	REPORTING_PARAMETERS_ACT     ("2.16.840.1.113883.10.20.27.3.23"),
	REPORTING_PARAMETERS_SECTION ("2.16.840.1.113883.10.20.27.2.6"),

	//unimplemented
	PERFORMANCE_RATE             ("2.16.840.1.113883.10.20.27.3.30"),
	ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS ("2.16.840.1.113883.10.20.27.3.29"),
	MEASURE_REFERENCE_RESULTS_CMS_V2 ("2.16.840.1.113883.10.20.27.3.17"),
	CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS ("2.16.840.1.113883.10.20.27.3.26"),
	ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2 ("2.16.840.1.113883.10.20.27.3.22"),
	MEASURE_DATA_CMS_V2 ("2.16.840.1.113883.10.20.27.3.16"),
	REPORTING_STRATUM_CMS ("2.16.840.1.113883.10.20.27.3.20"),
	SEX_SUPPLEMENTAL_DATA_ELEMENTAL_CMS_V2 ("2.16.840.1.113883.10.20.27.3.21"),
	RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2 ("2.16.840.1.113883.10.20.27.3.19"),
	PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2 ("2.16.840.1.113883.10.20.27.3.18"),
	PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2 ("2.16.840.1.113883.10.20.27.3.25"),
	MEASURE_SECTION ("2.16.840.1.113883.10.20.24.2.2"),
	MEASURE_SECTION_V2 ("2.16.840.1.113883.10.20.27.2.3"),
	CMS_AGGREGATE_COUNT ("2.16.840.1.113883.10.20.27.3.24"),
	//end unimplemented








	NULL_RETURN ("null.return"),
	QED ("Q.E.D"),
	DEFAULT                      ("default");

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
