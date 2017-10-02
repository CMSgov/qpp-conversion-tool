package gov.cms.qpp.test;

import org.junit.Assume;

import gov.cms.qpp.conversion.util.EnvironmentHelper;

public class VariableDependantTestHelper {

	public static void assumeIsPresent(String variable) {
		Assume.assumeTrue(EnvironmentHelper.isPresent(variable));
	}

	private VariableDependantTestHelper() {
	}

}
