package gov.cms.qpp.conversion.api.model;

import java.util.List;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

public class Details {

	private List<Detail> details;

	public List<Detail> getDetails() {
		return this.details;
	}

	public void setDetails(List<Detail> details) {
		this.details = details;
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
