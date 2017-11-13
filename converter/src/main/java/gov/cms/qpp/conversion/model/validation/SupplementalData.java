package gov.cms.qpp.conversion.model.validation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
	
	private enum SupplementalType {
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

	SupplementalData(String code, SupplementalType type) {
		this.code = code;
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public String getType() {
		return type.toString();
	}

	public static List<SupplementalData> getSupplementalDataListByType(String type) {
		return Arrays.stream(SupplementalData.values())
				.filter(s -> type.equalsIgnoreCase(s.getType()))
				.collect(Collectors.toList());
	}
}
