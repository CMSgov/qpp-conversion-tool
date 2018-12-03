package gov.cms.qpp.conversion.api.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ConstantsTest {
	@Test
	void testPrivateConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}
}