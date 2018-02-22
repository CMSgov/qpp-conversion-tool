package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.segmentation.QrdaScope;

import java.util.Set;

/**
 * Helper class used during scope validation
 */
public class Scopes {

	private Set<QrdaScope> qrdaScopes;
	private boolean valid;

	public Set<QrdaScope> getQrdaScopes() {
		return qrdaScopes;
	}

	public void setQrdaScopes(Set<QrdaScope> qrdaScopes) {
		this.qrdaScopes = qrdaScopes;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

}
