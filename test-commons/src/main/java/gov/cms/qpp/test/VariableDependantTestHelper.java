package gov.cms.qpp.test;

import org.junit.Assume;

public class VariableDependantTestHelper {

	public static boolean isPresent(String variable) {
		return System.getenv(variable) != null || System.getProperty(variable) != null;
	}

	public static void assumeIsPresent(String variable) {
		Assume.assumeTrue(isPresent(variable));
	}

	private VariableDependantTestHelper() {
	}

}
