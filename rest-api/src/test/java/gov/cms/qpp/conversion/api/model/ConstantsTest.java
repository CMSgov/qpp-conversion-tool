package gov.cms.qpp.conversion.api.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static com.google.common.truth.Truth.assertThat;

class ConstantsTest {
	@Test
	void testPrivateConstructor() throws Exception {
		Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	void testMapConstruction() {
		assertThat(Constants.ORG_ATTRIBUTE_MAP.get(Constants.RTI_ORG))
			.isEqualTo(Constants.DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE);
		assertThat(Constants.ORG_ATTRIBUTE_MAP.get(Constants.CPC_ORG))
			.isEqualTo(Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE);
	}
}