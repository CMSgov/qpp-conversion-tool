package gov.cms.qpp.conversion.api.model.security;

import com.google.common.base.MoreObjects;
import java.util.Map;

public class Organization {
	private String id;

	private String orgType;

	public Organization(String id, String orgType) {
		this.id = id;
		this.orgType = orgType;
	}

	public Organization(Map payload) {
		this.id = (String) payload.get("id");
		this.orgType = (String) payload.get("orgType");
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("orgType", orgType)
				.toString();
	}
}
