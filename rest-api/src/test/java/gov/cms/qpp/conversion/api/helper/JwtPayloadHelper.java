package gov.cms.qpp.conversion.api.helper;

public class JwtPayloadHelper {
	String id;
	String orgType;

	public String getId() {
		return id;
	}

	public String getOrgType() {
		return orgType;
	}

	public JwtPayloadHelper withId(String id) {
		this.id = id;
		return this;
	}

	public JwtPayloadHelper withOrgType(String orgType) {
		this.orgType = orgType;
		return this;
	}
}
