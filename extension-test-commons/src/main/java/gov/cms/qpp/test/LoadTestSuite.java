package gov.cms.qpp.test;

import org.junit.BeforeClass;

public class LoadTestSuite {

	@BeforeClass
	public static void ensureLoadTestsShouldRun() {
		VariableDependantTestHelper.assumeIsPresent("runLoadTests");
	}

}
