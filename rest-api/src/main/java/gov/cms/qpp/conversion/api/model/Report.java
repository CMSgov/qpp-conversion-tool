package gov.cms.qpp.conversion.api.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import gov.cms.qpp.conversion.model.error.Detail;

public class Report {

	private String programName;
	private String practiceSiteId;
	private Status status;
	private Long timestamp;
	private List<Detail> warnings;
	private List<Detail> errors;

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getPracticeSiteId() {
		return practiceSiteId;
	}

	public void setPracticeSiteId(String practiceSiteId) {
		this.practiceSiteId = practiceSiteId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
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
	public void setWarnings(List<Detail> warnings) {
		if (warnings == null) {
			this.warnings = null;
		} else {
			this.warnings = new ArrayList<>(warnings);
		}
	}

	/**
	 * Returns a defensive copy of the errors list (or an empty list if null).
	 */
	public List<Detail> getErrors() {
		if (errors == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(errors);
	}

	/**
	 * Stores a defensive copy of the provided list.
	 */
	public void setErrors(List<Detail> errors) {
		if (errors == null) {
			this.errors = null;
		} else {
			this.errors = new ArrayList<>(errors);
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

		Report that = (Report) o;

		boolean equals = Objects.equals(practiceSiteId, that.practiceSiteId);
		equals &= Objects.equals(programName, that.programName);
		equals &= Objects.equals(status, that.status);
		equals &= Objects.equals(timestamp, that.timestamp);
		equals &= Objects.equals(errors, that.errors);
		equals &= Objects.equals(warnings, that.warnings);
		return equals;
	}

	@Override
	public int hashCode() {
		return Objects.hash(practiceSiteId, status, timestamp, programName, errors, warnings);
	}

}
