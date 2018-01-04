package gov.cms.qpp.conversion.api.helper;

public class JwtPayloadHelper {
	String name;
	String orgType;

	public String getName() {
		return name;
	}

	public String getOrgType() {
		return orgType;
	}

	public JwtPayloadHelper withName(String name) {
		this.name = name;
		return this;
	}

	public JwtPayloadHelper withOrgType(String orgType) {
		this.orgType = orgType;
		return this;
	}
}
