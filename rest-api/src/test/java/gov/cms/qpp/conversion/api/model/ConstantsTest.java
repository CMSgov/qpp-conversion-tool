package gov.cms.qpp.conversion.api.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

class ConstantsTest {
	@Test
	void testPrivateConstructor() throws Exception {
		Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}
}