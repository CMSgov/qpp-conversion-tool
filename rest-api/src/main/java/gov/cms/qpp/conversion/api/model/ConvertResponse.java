package gov.cms.qpp.conversion.api.model;

import com.fasterxml.jackson.annotation.JsonRawValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	 * Returns an unmodifiable copy of the warnings list (never the live list).
	 */
	public List<Detail> getWarnings() {
		return (warnings == null)
				? Collections.emptyList()
				: new ArrayList<>(warnings);
	}

	/**
	 * Stores a copy of the passed‐in list so we never keep a caller’s mutable list.
	 */
	public void setWarnings(List<Detail> warnings) {
		this.warnings = (warnings == null)
				? null
				: new ArrayList<>(warnings);
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
		equals &= Objects.equals(getWarnings(), that.getWarnings());
		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, qpp, getWarnings());
	}
}
