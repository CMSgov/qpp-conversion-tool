package gov.cms.qpp.test;


import gov.cms.qpp.conversion.util.EnvironmentHelper;
import org.junit.jupiter.api.Assumptions;

class VariableDependantTestHelper {

	static void assumeIsPresent(String variable) {
		Assumptions.assumeTrue(EnvironmentHelper.isPresent(variable));
	}

	private VariableDependantTestHelper() {
	}

}
