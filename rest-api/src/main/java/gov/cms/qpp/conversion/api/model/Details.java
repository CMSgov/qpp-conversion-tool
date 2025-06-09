package gov.cms.qpp.conversion.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

/**
 * Wrapper for a list of {@link Detail} so that the internal list isn’t exposed.
 */
public class Details {

	private List<Detail> details;

	/**
	 * Returns a copy of the internal list (or null if none).
	 */
	public List<Detail> getDetails() {
		return details == null
				? null
				: new ArrayList<>(details);
	}

	public void setDetails(List<Detail> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object that) {
		if (that == this) {
			return true;
		}
		if (that == null || that.getClass() != getClass()) {
			return false;
		}
		return Objects.equals(details, ((Details) that).details);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(details);
	}
}
