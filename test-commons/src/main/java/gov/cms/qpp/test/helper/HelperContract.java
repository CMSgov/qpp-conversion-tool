package gov.cms.qpp.test.helper;

import java.lang.reflect.Constructor;

import org.junit.jupiter.api.Test;

public interface HelperContract {

	@Test
	default void testConstructorWorks() throws Exception {
		Class<?> type = getHelperClass();
		Constructor constructor = type.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	Class<?> getHelperClass();

}
