package gov.cms.qpp.conversion.model.validation;

import java.util.HashMap;
import java.util.Map;

public enum SupplementalData {

	ALASKAN_NATIVE_AMERICAN_INDIAN("1002-5"),
	ASIAN("2028-9"),
	AFRICAN_AMERICAN("2054-5"),
	HAWAIIAN_PACIFIC_ISLANDER("2076-8"),
	WHITE("2106-3"),
	OTHER_RACE("2131-1"),
	HISPANIC_LATINO("2135-2"),
	NOT_HISPANIC_LATINO("2186-5"),
	MALE("M"),
	FEMALE("F"),
	MEDICARE("A"),
	MEDICAID("B"),
	PRIVATE_HEALTH_INSURANCE("C"),
	OTHER_PAYER("D");

	SupplementalData(String code) {
		this.code = code;
	}

	private static final Map<String, String> SUPPLEMENTAL_DATA_MAP = new HashMap<>();

	static {
		for (SupplementalData supplementalData : SupplementalData.values()) {
			SUPPLEMENTAL_DATA_MAP.put(supplementalData.code, supplementalData.name());
		}
	}

	public static String getCategoryNameByCode(String code) {
		return SUPPLEMENTAL_DATA_MAP.get(code);
	}

	private final String code;
}
