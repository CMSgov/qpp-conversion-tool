package gov.cms.qpp.conversion.model;

import org.apache.commons.lang3.StringUtils;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.util.EnvironmentHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An enumeration of known templates IDs.
 */
public enum TemplateId {
	CLINICAL_DOCUMENT("2.16.840.1.113883.10.20.27.1.2", Extension.DECEMBER_2024, "Clinical Document"),
	PI_AGGREGATE_COUNT("2.16.840.1.113883.10.20.27.3.3"),
	IA_SECTION_V3("2.16.840.1.113883.10.20.27.2.4", Extension.DECEMBER_2020, "IA Section"),
	PI_SECTION_V3("2.16.840.1.113883.10.20.27.2.5", Extension.DECEMBER_2020, "PI Section"),
	MEASURE_PERFORMED("2.16.840.1.113883.10.20.27.3.27", Extension.SEPTEMBER_2016),
	PI_NUMERATOR_DENOMINATOR("2.16.840.1.113883.10.20.27.3.28", Extension.JUNE_2017, "PI Measure"),
	PI_NUMERATOR("2.16.840.1.113883.10.20.27.3.31", Extension.SEPTEMBER_2016),
	PI_DENOMINATOR("2.16.840.1.113883.10.20.27.3.32", Extension.SEPTEMBER_2016),
	IA_MEASURE("2.16.840.1.113883.10.20.27.3.33", Extension.SEPTEMBER_2016, "Improvement Activity"),
	REPORTING_PARAMETERS_ACT("2.16.840.1.113883.10.20.17.3.8"),
	MEASURE_DATA_CMS_V4("2.16.840.1.113883.10.20.27.3.16", Extension.MAY_2019),
	PERFORMANCE_RATE_PROPORTION_MEASURE("2.16.840.1.113883.10.20.27.3.25", Extension.MAY_2022),
	MEASURE_SECTION_V5("2.16.840.1.113883.10.20.27.2.3", Extension.MAY_2022, "Measure Section"),
	MEASURE_REFERENCE_RESULTS_CMS_V5("2.16.840.1.113883.10.20.27.3.17", Extension.MAY_2022, "Quality Measure"),
	PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS("2.16.840.1.113883.10.20.27.3.29", Extension.SEPTEMBER_2016),
	REPORTING_STRATUM_CMS("2.16.840.1.113883.10.20.27.3.4"),
	ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.7", Extension.SEPTEMBER_2016),
	SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.6", Extension.SEPTEMBER_2016),
	RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.8", Extension.SEPTEMBER_2016),
	PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2("2.16.840.1.113883.10.20.27.3.18", Extension.MAY_2018),
	CATEGORY_REPORT_V5("2.16.840.1.113883.10.20.27.2.1", Extension.DECEMBER_2020),

	//unimplemented
	CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS("2.16.840.1.113883.10.20.27.3.2"),

	//miscellaneous
	NULL_RETURN("null.return"),
	QED("Q.E.D"),
	PLACEHOLDER("placeholder"),
	DEFAULT("default"),
	UNIMPLEMENTED("unimplemented");

	/**
	 * Defined TemplateId Constants
	 */
	enum Extension {
		NONE(""),
		JUNE_2017("2017-06-01"),
		JULY_2017("2017-07-01"),
		JULY_2021("2021-07-01"),
		SEPTEMBER_2016("2016-09-01"),
		NOVEMBER_2016("2016-11-01"),
		MAY_2018("2018-05-01"),
		MAY_2019("2019-05-01"),
		MAY_2020("2020-05-01"),
		MAY_2022("2022-05-01"),
		DECEMBER_2020("2020-12-01"),
		DECEMBER_2024("2024-12-01");

		static final String STRICT_EXTENSION = "STRICT_EXTENSION";

		private final String value;

		Extension(String value) {
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
			if (!templateId.alwaysStrict) {
				extensionToTemplateId.putIfAbsent(null, templateId);
			}
		}
	}

	private final String root;
	private final Extension extension;
	private final boolean alwaysStrict;
	private final String humanReadableTitle;

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
		this(root, extension, null);
	}

	TemplateId(String root, Extension extension, String humanReadableTitle) {
		this.root = root;
		this.extension = extension;
		this.alwaysStrict = "CLINICAL_DOCUMENT".equals(this.name());
		this.humanReadableTitle = humanReadableTitle;
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
	 * @return The human readable title, if any.
	 */
	public String getHumanReadableTitle() {
		return humanReadableTitle;
	}

	/**
	 * String representation of this template id
	 *
	 * @param context allows historical check
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
	 * @param context allows historical check
	 * @return The template ID if found, else a defaulted TemplateId. The TemplateId will be
	 * {@code TemplateId.UNIMPLEMENTED}.
	 */
	public static TemplateId getTemplateId(final String root, final String extension, final Context context) {
		TemplateId defaultTemplate = TemplateId.UNIMPLEMENTED;
		Map<String, TemplateId> extensionsToTemplateId = ROOT_AND_TO_TEMPLATE_ID.get(root);
		Function<Boolean, TemplateId> templateIdFunction = condition -> condition
			? extensionsToTemplateId.getOrDefault(extension, defaultTemplate) :
			extensionsToTemplateId.getOrDefault(null, defaultTemplate);
		TemplateId retrieved = null;

		if (extensionsToTemplateId == null) {
			retrieved = defaultTemplate;
		} else if (context.isHistorical()) {
			retrieved = templateIdFunction.apply(CLINICAL_DOCUMENT.root.equals(root));
		} else {
			retrieved = templateIdFunction.apply(
				CLINICAL_DOCUMENT.root.equals(root) || EnvironmentHelper.isPresent(Extension.STRICT_EXTENSION));
		}

		return retrieved;
	}

	/**
	 * Creates the templateId string in the same fashion that this enumeration does.
	 *
	 * @param root The root part of the templateId.
	 * @param extension The extension part of the templateId.
	 * @param context allows historical check
	 * @return A string that concatenates the arguments the same way the enumeration does.
	 */
	static String generateTemplateIdString(String root, String extension, Context context) {
		String templateId = root;

		if (!context.isHistorical() && !StringUtils.isEmpty(extension)) {
			templateId += (":" + extension);
		}
		return templateId;
	}
}
