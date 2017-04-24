package gov.cms.qpp;

import gov.cms.qpp.conversion.ConversionEntry;
import org.junit.Before;

import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * handle generic test operations
 */
public class BaseTest {
	/**
	 * Ensure empty scope before each test.
	 *
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	@Before
	public void preCleanup() throws NoSuchFieldException, IllegalAccessException {
		Field scope = ConversionEntry.class.getDeclaredField("scope");
		scope.setAccessible(true);
		scope.set(null, new HashSet<>());
	}
}
