package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.test.enums.EnumContract;

class DecodeResultTest implements EnumContract {

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return DecodeResult.class;
	}

}