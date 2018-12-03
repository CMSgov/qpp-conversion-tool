package gov.cms.qpp.conversion.model.validation;

import org.junit.jupiter.api.Nested;

import gov.cms.qpp.test.enums.EnumContract;

class SupplementalDataTest implements EnumContract {

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return SupplementalData.class;
	}

	@Nested
	static class SupplementalTypeTest implements EnumContract {

		@Override
		public Class<? extends Enum<?>> getEnumType() {
			return SupplementalData.SupplementalType.class;
		}

	}

}
