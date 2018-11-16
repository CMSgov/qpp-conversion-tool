package gov.cms.qpp.conversion.api.model;

import java.util.List;
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

	public List<Detail> getWarnings() {
		return warnings;
	}

	public void setWarnings(List<Detail> warnings) {
		this.warnings = warnings;
	}

	public List<Detail> getErrors() {
		return errors;
	}

	public void setErrors(List<Detail> errors) {
		this.errors = errors;
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
