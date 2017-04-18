package gov.cms.qpp.conversion.model;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {

	CLINICAL_DOCUMENT("2.16.840.1.113883.10.20.27.1.2", "2016-11-01"),
	ACI_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION("2.16.840.1.113883.10.20.27.2.4", "2016-09-01"),
	ACI_SECTION("2.16.840.1.113883.10.20.27.2.5", "2016-09-01"),
	ACI_MEASURE_PERFORMED("2.16.840.1.113883.10.20.27.3.27", "2016-09-01"),
	ACI_PROPORTION("2.16.840.1.113883.10.20.27.3.28", "2016-09-01"),
	ACI_NUMERATOR("2.16.840.1.113883.10.20.27.3.31", "2016-09-01"),
	ACI_DENOMINATOR("2.16.840.1.113883.10.20.27.3.32", "2016-09-01"),
	IA_MEASURE("2.16.840.1.113883.10.20.27.3.33", "2016-09-01"),
	REPORTING_PARAMETERS_ACT("2.16.840.1.113883.10.20.27.3.23", "2016-11-01"),
	REPORTING_PARAMETERS_SECTION("2.16.840.1.113883.10.20.27.2.6", "2016-11-01"),

	//unimplemented
	PERFORMANCE_RATE("2.16.840.1.113883.10.20.27.3.30", "2016-09-01"),
	ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS("2.16.840.1.113883.10.20.27.3.29", "2016-09-01"),
	MEASURE_REFERENCE_RESULTS_CMS_V2("2.16.840.1.113883.10.20.27.3.17", "2016-11-01"),
	CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS("2.16.840.1.113883.10.20.27.3.26"),
	ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.22", "2016-11-01"),
	MEASURE_DATA_CMS_V2("2.16.840.1.113883.10.20.27.3.16", "2016-11-01"),
	REPORTING_STRATUM_CMS("2.16.840.1.113883.10.20.27.3.20"),
	SEX_SUPPLEMENTAL_DATA_ELEMENTAL_CMS_V2("2.16.840.1.113883.10.20.27.3.21", "2016-11-01"),
	RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.19", "2016-11-01"),
	PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.18", "2016-11-01"),
	PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2("2.16.840.1.113883.10.20.27.3.25", "2016-11-01"),
	MEASURE_SECTION("2.16.840.1.113883.10.20.24.2.2"),
	MEASURE_SECTION_V2("2.16.840.1.113883.10.20.27.2.3", "2016-11-01"),
	NATIONAL_PROVIDER("2.16.840.1.113883.4.6"),
	CMS_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.24"),

	//miscellaneous
	NULL_RETURN("null.return"),
	QED("Q.E.D"),
	PLACEHOLDER("placeholder"),
	DEFAULT("default");

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
		return generateTemplateIdString(root, extension);
	}

	/**
	 * Returns the enumeration for the specified ID.
	 *
	 * @param id The complete template ID (root + ":" + extension).
	 * @return The template ID if found.  Else {@code TemplateId.DEFAULT}.
	 */
	public static TemplateId getTypeById(final String id) {
		for (TemplateId type : TemplateId.values()) {
			if (type.getTemplateId().equals(id)) {
				return type;
			}
		}

		return TemplateId.DEFAULT;
	}

	/**
	 * Returns the enumeration for the specified root and extension.
	 *
	 * @param root The root part of the templateId.
	 * @param extension The extension part of the templateId.
	 * @return The template ID if found.  Else {@code TemplateId.DEFAULT}.
	 */
	public static TemplateId getTypeById(final String root, final String extension) {
		for (TemplateId currentTemplateId : TemplateId.values()) {
			if (currentTemplateId.getRoot().equals(root) && currentTemplateId.getExtension().equals(extension)) {
				return currentTemplateId;
			}
		}

		return TemplateId.DEFAULT;
	}

	/**
	 * Creates the templateId string in the same fashion that this enumeration does.
	 *
	 * @param root The root part of the templateId.
	 * @param extension The extension part of the templateId.
	 * @return A string that concatenates the arguments the same way the enumeration does.
	 */
	public static String generateTemplateIdString(final String root, final String extension) {
		String templateId = root;

		if (extension != null && !extension.isEmpty()) {
			templateId += (":" + extension);
		}

		return templateId;
	}
}
