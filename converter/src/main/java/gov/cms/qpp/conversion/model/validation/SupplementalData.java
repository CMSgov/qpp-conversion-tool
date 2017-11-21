package gov.cms.qpp.conversion.model.validation;

import gov.cms.qpp.conversion.model.TemplateId;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An Enumeration of the known Supplemental Data values and types.
 */
public enum SupplementalData {

	ALASKAN_NATIVE_AMERICAN_INDIAN("1002-5", SupplementalType.RACE),
	ASIAN("2028-9", SupplementalType.RACE),
	AFRICAN_AMERICAN("2054-5", SupplementalType.RACE),
	HAWAIIAN_PACIFIC_ISLANDER("2076-8", SupplementalType.RACE),
	WHITE("2106-3", SupplementalType.RACE),
	OTHER_RACE("2131-1", SupplementalType.RACE),
	HISPANIC_LATINO("2135-2", SupplementalType.ETHNICITY),
	NOT_HISPANIC_LATINO("2186-5", SupplementalType.ETHNICITY),
	MALE("M", SupplementalType.SEX),
	FEMALE("F", SupplementalType.SEX),
	MEDICARE("A", SupplementalType.PAYER),
	MEDICAID("B", SupplementalType.PAYER),
	PRIVATE_HEALTH_INSURANCE("C", SupplementalType.PAYER),
	OTHER_PAYER("D", SupplementalType.PAYER);

	private final String code;
	private final SupplementalType type;

	protected static final EnumMap<SupplementalType, TemplateId> SUPPLEMENTAL_TYPES =
			new EnumMap<>(SupplementalType.class);

	/**
	 * Static map creation of Supplemental Types to their designated template id's
	 */
	static {
		SUPPLEMENTAL_TYPES.put(SupplementalType.RACE, TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2);
		SUPPLEMENTAL_TYPES.put(SupplementalType.SEX, TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2);
		SUPPLEMENTAL_TYPES.put(SupplementalType.ETHNICITY,
				TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2);
		SUPPLEMENTAL_TYPES.put(SupplementalType.PAYER, TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2);
	}

	/**
	 * Defined Supplemental Data Types
	 */
	public enum SupplementalType {
		RACE("R"),
		SEX("S"),
		ETHNICITY("E"),
		PAYER("P");

		private final String type;

		SupplementalType(String type) {
			this.type = type;
		}
		@Override
		public String toString() {
			return this.type;
		}

	}

	/**
	 * Constructs a SupplementalData object with code and SupplementalType
	 *
	 * @param code Value of SupplementalData
	 * @param type Type of SupplementalData
	 */
	SupplementalData(String code, SupplementalType type) {
		this.code = code;
		this.type = type;
	}

	/**
	 * Retrieves the code
	 *
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Retrieves the value of SupplementalType
	 *
	 * @return SuplementalType value
	 */
	public String getType() {
		return type.toString();
	}

	/**
	 * Retrieves an {@link Set} which contains the SupplementalData values of a specific type
	 *
	 * @param type Supplemental Type to filter by
	 * @return {@link Set} of SupplementalData
	 */
	public static Set<SupplementalData> getSupplementalDataSetByType(String type) {
		return EnumSet.copyOf(Arrays.stream(SupplementalData.values())
				.filter(s -> type.equalsIgnoreCase(s.getType()))
				.collect(Collectors.toSet()));
	}

	/**
	 * Retrives the static {@link Map} of SupplementalTypes to {@link TemplateId}
	 *
	 * @return static supplemental type map
	 */
	public static Map<SupplementalType, TemplateId> getSupplementalTypeMapToTemplateId(){
		return SUPPLEMENTAL_TYPES.clone();
	}
}
