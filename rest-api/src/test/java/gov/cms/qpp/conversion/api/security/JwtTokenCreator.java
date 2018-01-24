package gov.cms.qpp.conversion.api.security;

import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;

public class JwtTokenCreator {
	public static void main(String... args) {

		String orgName = JwtAuthorizationFilter.DEFAULT_ORG_NAME;

		if (args.length >= 1) {
			orgName = args[0];
		}

		System.out.println("Using organization " + orgName);

		JwtPayloadHelper payload = new JwtPayloadHelper()
			.withName(orgName)
			.withOrgType("registry");

		System.out.println(JwtTestHelper.createJwt(payload));
	}
}
