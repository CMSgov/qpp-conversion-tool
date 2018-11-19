package gov.cms.qpp.conversion.api.model;

import java.util.List;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

public class ConvertResponse {

	private Object qpp;
	private List<Detail> warnings;

	public Object getQpp() {
		return qpp;
	}

	public void setQpp(Object qpp) {
		this.qpp = qpp;
	}

	public List<Detail> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Detail> warnings) {
		this.warnings = warnings;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || o.getClass() != getClass()) {
			return false;
		}

		ConvertResponse that = (ConvertResponse) o;

		boolean equals = Objects.equals(qpp, that.qpp);
		equals &= Objects.equals(warnings, that.warnings);
		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(qpp, warnings);
	}

}
