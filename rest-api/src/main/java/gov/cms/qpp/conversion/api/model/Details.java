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

	/**
	 * Stores a defensive copy of the given list to avoid exposing our internal representation.
	 *
	 * @param details the list of Detail instances (or null)
	 */
	public void setDetails(List<Detail> details) {
		this.details = (details == null)
				? null
				: new ArrayList<>(details);
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
