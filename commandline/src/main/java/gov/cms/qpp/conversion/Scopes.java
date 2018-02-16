package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.segmentation.QrdaScope;

import java.util.Set;

public class Scopes {

	private Set<QrdaScope> scopes;
	private boolean valid;

	public Set<QrdaScope> getScopes() {
		return scopes;
	}

	public void setScopes(Set<QrdaScope> scopes) {
		this.scopes = scopes;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
