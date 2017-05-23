package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.ConversionEntry;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {
	CLINICAL_DOCUMENT("2.16.840.1.113883.10.20.27.1.2", Extension.NOVEMBER),
	ACI_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION("2.16.840.1.113883.10.20.27.2.4", Extension.SEPTEMBER),
	ACI_SECTION("2.16.840.1.113883.10.20.27.2.5", Extension.SEPTEMBER),
	MEASURE_PERFORMED("2.16.840.1.113883.10.20.27.3.27", Extension.SEPTEMBER),
	ACI_NUMERATOR_DENOMINATOR("2.16.840.1.113883.10.20.27.3.28", Extension.SEPTEMBER),
	ACI_NUMERATOR("2.16.840.1.113883.10.20.27.3.31", Extension.SEPTEMBER),
	ACI_DENOMINATOR("2.16.840.1.113883.10.20.27.3.32", Extension.SEPTEMBER),
	IA_MEASURE("2.16.840.1.113883.10.20.27.3.33", Extension.SEPTEMBER),
	REPORTING_PARAMETERS_ACT("2.16.840.1.113883.10.20.27.3.23", Extension.NOVEMBER),
	MEASURE_DATA_CMS_V2("2.16.840.1.113883.10.20.27.3.16", Extension.NOVEMBER),
	MEASURE_SECTION_V2("2.16.840.1.113883.10.20.27.2.3", Extension.NOVEMBER),
	REPORTING_PARAMETERS_SECTION("2.16.840.1.113883.10.20.27.2.6", Extension.NOVEMBER),
	MEASURE_REFERENCE_RESULTS_CMS_V2("2.16.840.1.113883.10.20.27.3.17", Extension.NOVEMBER),
	ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS("2.16.840.1.113883.10.20.27.3.29", Extension.SEPTEMBER),

	//unimplemented
	PERFORMANCE_RATE("2.16.840.1.113883.10.20.27.3.30", Extension.SEPTEMBER),
	CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS("2.16.840.1.113883.10.20.27.3.26"),
	REPORTING_STRATUM_CMS("2.16.840.1.113883.10.20.27.3.20"),
	PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2("2.16.840.1.113883.10.20.27.3.25", Extension.NOVEMBER),
	CMS_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.24"),

	//miscellaneous
	NULL_RETURN("null.return"),
	QED("Q.E.D"),
	PLACEHOLDER("placeholder"),
	DEFAULT("default");

	/**
	 * Defined TemplateId Constants
	 */
	private enum Extension {
		NONE(""),
		SEPTEMBER("2016-09-01"),
		NOVEMBER("2016-11-01");

		private final String extension;

		private Extension(String extension) {
			this.extension = extension;
		}

		@Override
		public String toString()
		{
			return this.extension;
		}
	}

	private final String root;
	private final Extension extension;

	/**
	 * Constructs a TemplateId with just a root.
	 *
	 * @param root The root of the template ID.  Normally numbers with decimal points in between.
	 */
	TemplateId(String root) {
		this(root, Extension.NONE);
	}

	/**
	 * Constructs a TemplateId with a root and an extension.
	 *
	 * @param root The root of the template ID.  Normally numbers with decimal points in between.
	 * @param extension The extension of the template ID.  Normally a date.
	 */
	TemplateId(String root, Extension extension) {
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
		return extension.toString();
	}

	/**
	 * @return The complete template ID which includes a concatenation of the root followed by a colon followed by the
	 * extension.
	 */
	public String getTemplateId() {
		return generateTemplateIdString(root, getExtension());
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
	public static TemplateId getTypeById(String root, String extension) {
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
	public static String generateTemplateIdString(String root, String extension) {
		String templateId = root;

		if (!ConversionEntry.isHistorical() && extension != null && !extension.isEmpty()) {
			templateId += (":" + extension);
		}
		return templateId;
	}
}
