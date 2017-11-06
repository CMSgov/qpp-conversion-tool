package gov.cms.qpp.test;

import org.junit.jupiter.api.BeforeAll;

/**
 * Ensures that an environment variable or system property {@code runLoadTests} is present, or skips the tests in the class
 */
public abstract class LoadTestSuite {

	@BeforeAll
	public static void ensureLoadTestsShouldRun() {
		VariableDependantTestHelper.assumeIsPresent("runLoadTests");
	}

}
