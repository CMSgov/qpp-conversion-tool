package gov.cms.qpp.conversion.api.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

public class ConvertResponse {

	private String location;

	@JsonRawValue
	private String qpp;

	private List<Detail> warnings;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Object getQpp() {
		return qpp;
	}

	public void setQpp(String qpp) {
		this.qpp = qpp;
	}

	/**
	 * Returns a defensive copy of the warnings list (or an empty list if null).
	 */
	public List<Detail> getWarnings() {
		if (warnings == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(warnings);
	}

	/**
	 * Stores a defensive copy of the provided list.
	 */
	@SuppressFBWarnings(
			value = "EI_EXPOSE_REP2",
			justification = "Defensive copy prevents external modification"
	)
	public void setWarnings(List<Detail> warnings) {
		if (warnings == null) {
			this.warnings = null;
		} else {
			this.warnings = new ArrayList<>(warnings);
		}
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

		boolean equals = Objects.equals(location, that.location);
		equals &= Objects.equals(qpp, that.qpp);
		equals &= Objects.equals(warnings, that.warnings);
		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, qpp, warnings);
	}

}
