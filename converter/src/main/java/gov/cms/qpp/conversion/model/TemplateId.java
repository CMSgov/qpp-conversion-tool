package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.Context;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Strings;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {
	CLINICAL_DOCUMENT("2.16.840.1.113883.10.20.27.1.2", Extension.JULY_2017),
	ACI_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION("2.16.840.1.113883.10.20.27.2.4", Extension.JUNE_2017),
	ACI_SECTION("2.16.840.1.113883.10.20.27.2.5", Extension.JUNE_2017),
	MEASURE_PERFORMED("2.16.840.1.113883.10.20.27.3.27", Extension.SEPTEMBER_2016),
	ACI_NUMERATOR_DENOMINATOR("2.16.840.1.113883.10.20.27.3.28", Extension.JUNE_2017),
	ACI_NUMERATOR("2.16.840.1.113883.10.20.27.3.31", Extension.SEPTEMBER_2016),
	ACI_DENOMINATOR("2.16.840.1.113883.10.20.27.3.32", Extension.SEPTEMBER_2016),
	IA_MEASURE("2.16.840.1.113883.10.20.27.3.33", Extension.SEPTEMBER_2016),
	REPORTING_PARAMETERS_ACT("2.16.840.1.113883.10.20.17.3.8"),
	MEASURE_DATA_CMS_V2("2.16.840.1.113883.10.20.27.3.16", Extension.NOVEMBER_2016),
	PERFORMANCE_RATE_PROPORTION_MEASURE("2.16.840.1.113883.10.20.27.3.25", Extension.NOVEMBER_2016),
	MEASURE_SECTION_V2("2.16.840.1.113883.10.20.27.2.3", Extension.JULY_2017),
	MEASURE_REFERENCE_RESULTS_CMS_V2("2.16.840.1.113883.10.20.27.3.17", Extension.NOVEMBER_2016),
	ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS("2.16.840.1.113883.10.20.27.3.29", Extension.SEPTEMBER_2016),
	REPORTING_STRATUM_CMS("2.16.840.1.113883.10.20.27.3.20"),
	ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.22", Extension.NOVEMBER_2016),
	SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.21", Extension.NOVEMBER_2016),
	RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.19", Extension.NOVEMBER_2016),
	PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.18", Extension.NOVEMBER_2016),

	//unimplemented
	CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS("2.16.840.1.113883.10.20.27.3.26"),

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
		JUNE_2017("2017-06-01"),
		JULY_2017("2017-07-01"),
		SEPTEMBER_2016("2016-09-01"),
		NOVEMBER_2016("2016-11-01");

		private final String value;

		private Extension(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}

	private static final Map<String, Map<String, TemplateId>> ROOT_AND_TO_TEMPLATE_ID = new HashMap<>();

	static {
		for (TemplateId templateId : TemplateId.values()) {
			Map<String, TemplateId> extensionToTemplateId =
					ROOT_AND_TO_TEMPLATE_ID.computeIfAbsent(templateId.root, ignore -> new HashMap<>());

			extensionToTemplateId.put(templateId.extension.toString(), templateId);
			extensionToTemplateId.putIfAbsent(null, templateId);
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
	public String getTemplateId(Context context) {
		return generateTemplateIdString(root, getExtension(), context);
	}

	/**
	 * Returns the enumeration for the specified root and extension.
	 *
	 * @param root The root part of the templateId.
	 * @param extension The extension part of the templateId.
	 * @return The template ID if found.  Else {@code TemplateId.DEFAULT}.
	 */
	public static TemplateId getTemplateId(String root, String extension, Context context) {
		Map<String, TemplateId> extensionsToTemplateId = ROOT_AND_TO_TEMPLATE_ID.get(root);

		if (extensionsToTemplateId == null) {
			return TemplateId.DEFAULT;
		}

		if (context.isHistorical()) {
			return extensionsToTemplateId.getOrDefault(null, TemplateId.DEFAULT);
		}

		return extensionsToTemplateId.getOrDefault(extension, TemplateId.DEFAULT);
	}

	/**
	 * Creates the templateId string in the same fashion that this enumeration does.
	 *
	 * @param root The root part of the templateId.
	 * @param extension The extension part of the templateId.
	 * @return A string that concatenates the arguments the same way the enumeration does.
	 */
	static String generateTemplateIdString(String root, String extension, Context context) {
		String templateId = root;

		if (!context.isHistorical() && !Strings.isNullOrEmpty(extension)) {
			templateId += (":" + extension);
		}
		return templateId;
	}
}
