package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.ConversionEntry;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {
	MULTIPLE_TINS("2.16.840.1.113883.10.20.27.1.1", Constants.SEPTEMBER_EXTENSION),
	CLINICAL_DOCUMENT("2.16.840.1.113883.10.20.27.1.2", Constants.NOVEMBER_EXTENSION),
	ACI_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION("2.16.840.1.113883.10.20.27.2.4", Constants.SEPTEMBER_EXTENSION),
	ACI_SECTION("2.16.840.1.113883.10.20.27.2.5", Constants.SEPTEMBER_EXTENSION),
	MEASURE_PERFORMED("2.16.840.1.113883.10.20.27.3.27", Constants.SEPTEMBER_EXTENSION),
	ACI_NUMERATOR_DENOMINATOR("2.16.840.1.113883.10.20.27.3.28", Constants.SEPTEMBER_EXTENSION),
	ACI_NUMERATOR("2.16.840.1.113883.10.20.27.3.31", Constants.SEPTEMBER_EXTENSION),
	ACI_DENOMINATOR("2.16.840.1.113883.10.20.27.3.32", Constants.SEPTEMBER_EXTENSION),
	IA_MEASURE("2.16.840.1.113883.10.20.27.3.33", Constants.SEPTEMBER_EXTENSION),
	REPORTING_PARAMETERS_ACT("2.16.840.1.113883.10.20.27.3.23", Constants.NOVEMBER_EXTENSION),
	MEASURE_DATA_CMS_V2("2.16.840.1.113883.10.20.27.3.16", Constants.NOVEMBER_EXTENSION),
	MEASURE_SECTION_V2("2.16.840.1.113883.10.20.27.2.3", Constants.NOVEMBER_EXTENSION),
	REPORTING_PARAMETERS_SECTION("2.16.840.1.113883.10.20.27.2.6", Constants.NOVEMBER_EXTENSION),
	MEASURE_REFERENCE_RESULTS_CMS_V2("2.16.840.1.113883.10.20.27.3.17", Constants.NOVEMBER_EXTENSION),
	ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS("2.16.840.1.113883.10.20.27.3.29", Constants.SEPTEMBER_EXTENSION),

	//unimplemented
	PERFORMANCE_RATE("2.16.840.1.113883.10.20.27.3.30", Constants.SEPTEMBER_EXTENSION),
	CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS("2.16.840.1.113883.10.20.27.3.26"),
	ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.22", Constants.NOVEMBER_EXTENSION),
	REPORTING_STRATUM_CMS("2.16.840.1.113883.10.20.27.3.20"),
	SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.21", Constants.NOVEMBER_EXTENSION),
	RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.19", Constants.NOVEMBER_EXTENSION),
	PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.18", Constants.NOVEMBER_EXTENSION),
	PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2("2.16.840.1.113883.10.20.27.3.25", Constants.NOVEMBER_EXTENSION),
	MEASURE_SECTION("2.16.840.1.113883.10.20.24.2.2"),
	CMS_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.24"),

	//miscellaneous
	NULL_RETURN("null.return"),
	QED("Q.E.D"),
	PLACEHOLDER("placeholder"),
	DEFAULT("default");

	/**
	 * Defined TemplateId Constants
	 */
	private static class Constants {
		public static final String SEPTEMBER_EXTENSION = "2016-09-01";
		public static final String NOVEMBER_EXTENSION = "2016-11-01";

		private Constants() {
			//Constructor to ensure Constants will not be instantiated
		}
	}

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
			if (ConversionEntry.isHistorical() && currentTemplateId.getRoot().equals(root)) {
				return currentTemplateId;
			} else if (currentTemplateId.getRoot().equals(root) && currentTemplateId.getExtension().equals(extension)) {
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

		if (!ConversionEntry.isHistorical() && extension != null && !extension.isEmpty()) {
			templateId += (":" + extension);
		}
		return templateId;
	}
}
