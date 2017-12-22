package gov.cms.qpp.conversion.api.model.security;

public class Organization {
	private String id;
	private String orgType;

	public Organization(String id, String orgType) {
		this.id = id;
		this.orgType = orgType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
}
