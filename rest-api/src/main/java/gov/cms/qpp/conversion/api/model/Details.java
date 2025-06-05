package gov.cms.qpp.conversion.api.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

public class Details {

	private List<Detail> details;

	/**
	 * Returns a defensive copy of the details list (or an empty list if null).
	 */
	public List<Detail> getDetails() {
		if (details == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(details);
	}

	/**
	 * Stores a defensive copy of the provided list.
	 */
	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "Defensive copy prevents external modification"
	)
	public void setDetails(List<Detail> details) {
		if (details == null) {
			this.details = null;
		} else {
			this.details = new ArrayList<>(details);
		}
	}

	@Override
	public boolean equals(Object that) {
		if (that == this) {
			return true;
		}

		if (that == null) {
			return false;
		}

		if (that.getClass() != getClass()) {
			return false;
		}

		return Objects.equals(details, ((Details) that).details);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(details);
	}

}
