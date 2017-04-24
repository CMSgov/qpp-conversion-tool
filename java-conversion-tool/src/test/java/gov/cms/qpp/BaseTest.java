package gov.cms.qpp;

import gov.cms.qpp.conversion.ConversionEntry;
import org.junit.Before;

import java.lang.reflect.Field;
import java.util.HashSet;


public class BaseTest {
	@Before
	public void preCleanup() throws NoSuchFieldException, IllegalAccessException {
		Field scope = ConversionEntry.class.getDeclaredField("scope");
		scope.setAccessible(true);
		scope.set(null, new HashSet<>());
	}
}
